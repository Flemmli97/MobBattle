package com.flemmli97.mobbattle.items;

import java.util.List;

import javax.annotation.Nullable;

import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.items.entitymanager.Team;

import net.minecraft.block.BlockFlowingFluid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemExtendedSpawnEgg extends Item{
	
	public static final String tagString = MobBattle.MODID + ":Entity";

	public ItemExtendedSpawnEgg()
	{
		super(new Item.Properties().group(MobBattle.customTab));
        this.setRegistryName(new ResourceLocation(MobBattle.MODID, "egg_ex"));
    }
    
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
		list.add(new TextComponentString(TextFormatting.AQUA + "Left click an entity to save it. Shift while doing saves nbt too."));
		if(ItemExtendedSpawnEgg.hasSavedEntity(stack))
		{
			NBTTagCompound compound = stack.getTag().getCompound(tagString);
			String entity = EntityType.getById(compound.getString("id")).getTranslationKey();
			if(entity!=null)
			{
				String entityName = compound.hasKey("CustomName")?compound.getString("CustomName"):I18n.format(entity);
				list.add(new TextComponentString(TextFormatting.GOLD + "Spawns " + entityName + (compound.size()>1?" (+NBT)":"")));
			}
		}
	}
	
	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		if(entity instanceof EntityLiving)
		{
			if(player.abilities.isCreativeMode )//|| player.canUseCommand(2, ""))
			{
				EntityLiving e = (EntityLiving) entity;
				boolean nbt = false;
				NBTTagCompound compound = stack.getTag();
				if(compound==null)
					compound = new NBTTagCompound();
				NBTTagCompound tag = new NBTTagCompound();
				if(player.isSneaking())
				{
					e.writeUnlessPassenger(tag);
					this.removeMobSpecifigTags(tag);
					nbt=true;
				}
				else
				{
			        String name = e.getEntityString();
			        if(name!=null)
			            tag.setString("id", name);
					/*if(CommonProxy.mca && e instanceof EntityVillagerMCA)
					{
						tag.setInteger("MCAGender", ((EntityVillagerMCA)e).attributes.getGender().getId());
					}*/
				}
				compound.setTag(tagString, tag);
				stack.setTag(compound);
	
				if (!player.world.isRemote)
				{
					player.sendMessage(new TextComponentString(TextFormatting.GOLD + "Saved Entity" + (nbt?" + nbt":"")));
				}
			}
			else
			{
				player.sendMessage(new TextComponentString(TextFormatting.DARK_RED + "Needs to be in creative mode to copy entity"));
			}
		}
	    return true;
	}
	
	@Override
	public EnumActionResult onItemUse(ItemUseContext ctx) {
        if (ctx.getWorld().isRemote)
            return EnumActionResult.SUCCESS;
		ItemStack itemstack = ctx.getItem();
        if (!ctx.getPlayer().canPlayerEdit(ctx.getPos().offset(ctx.getFace()), ctx.getFace(), itemstack))
            return EnumActionResult.FAIL;
    	//TODO Look for a way of applying nbt to spawner
        /*IBlockState iblockstate = world.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (block == Blocks.MOB_SPAWNER)
        {
            TileEntity tileentity = world.getTileEntity(pos);

            if (tileentity instanceof TileEntityMobSpawner)
            {
                MobSpawnerBaseLogic mobspawnerbaselogic = ((TileEntityMobSpawner)tileentity).getSpawnerBaseLogic();
                mobspawnerbaselogic.setEntityId(getNamedIdFrom(itemstack));
                mobspawnerbaselogic.getCachedEntity()
                tileentity.markDirty();
                world.notifyBlockUpdate(pos, iblockstate, iblockstate, 3);

                if (!player.capabilities.isCreativeMode)
                {
                    itemstack.shrink(1);
                }

                return EnumActionResult.SUCCESS;
            }
        }*/

        IBlockState iblockstate = ctx.getWorld().getBlockState(ctx.getPos());

        BlockPos blockpos;
        if (iblockstate.getCollisionShape(ctx.getWorld(), ctx.getPos()).isEmpty()) {
           blockpos = ctx.getPos();
        } else {
           blockpos = ctx.getPos().offset(ctx.getFace());
        }
        
        Entity entity = ItemExtendedSpawnEgg.spawnEntity(ctx.getWorld(), itemstack, blockpos.getX() + 0.5D, blockpos.getY(), blockpos.getZ() + 0.5D);

        if (entity != null)
        {
        	if(!ctx.getPlayer().abilities.isCreativeMode)
        		itemstack.shrink(1);
            if(itemstack.hasDisplayName() && entity instanceof EntityCreature)
    		{
                Team.updateEntity(itemstack.getDisplayName().getUnformattedComponentText(), (EntityCreature) entity);
    		}
        }
        return EnumActionResult.SUCCESS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack itemstack = player.getHeldItem(hand);
        if (world.isRemote || !itemstack.hasTag() || !itemstack.getTag().hasKey(MobBattle.MODID + ":Entity"))
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
        else
        {
            RayTraceResult raytraceresult = this.rayTrace(world, player, true);
            
            if (raytraceresult != null && raytraceresult.type == RayTraceResult.Type.BLOCK)
            {
                BlockPos blockpos = raytraceresult.getBlockPos();

                if (!(world.getBlockState(blockpos).getBlock() instanceof BlockFlowingFluid))
                {
                    return new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack);
                }
                else if (world.isBlockModifiable(player, blockpos) && player.canPlayerEdit(blockpos, raytraceresult.sideHit, itemstack))
                {
                    Entity entity = ItemExtendedSpawnEgg.spawnEntity(world, itemstack, blockpos.getX() + 0.5D, blockpos.getY() + 0.5D, blockpos.getZ() + 0.5D);
                    if (entity != null)
                    {
                    	if(!player.abilities.isCreativeMode)
                    		itemstack.shrink(1);
                        if(itemstack.hasDisplayName() && entity instanceof EntityCreature)
                		{
                            Team.updateEntity(itemstack.getDisplayName().getUnformattedComponentText(), (EntityCreature) entity);
                		}
                        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
                    }
                }
                else
                {
                    return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
                }
            }
        }
        return new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack);
	}

	public static Entity spawnEntity(World world, ItemStack stack, double x, double y, double z)
	{
		Entity entity = null;
		if(ItemExtendedSpawnEgg.hasSavedEntity(stack))
		{
			entity = EntityType.create(stack.getTag().getCompound(tagString), world);
	        if (entity instanceof EntityLiving)
	        {
	            EntityLiving entityliving = (EntityLiving)entity;
	            /*if(CommonProxy.mca && entityliving instanceof EntityVillagerMCA && stack.getTag().getCompound(tagString).hasKey("MCAGender"))
	            {
	            	EntityVillagerMCA villager = (EntityVillagerMCA) entityliving;
					villager.attributes.setGender(EnumGender.byId(stack.getTag().getCompound(tagString).getInt("MCAGender")));
					villager.attributes.assignRandomName();
					villager.attributes.assignRandomProfession();
					villager.attributes.assignRandomPersonality();
					villager.attributes.assignRandomSkin();
	            }*/
	            entity.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
	            entityliving.rotationYawHead = entityliving.rotationYaw;
	            entityliving.renderYawOffset = entityliving.rotationYaw;
	            entityliving.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entityliving)), null, null);
	            world.spawnEntity(entity);
	            entityliving.playAmbientSound();
	        }
		}
        return entity;
	}
	
	private static boolean hasSavedEntity(ItemStack stack)
	{
		if(stack.hasTag() && stack.getTag().hasKey(tagString) && stack.getTag().getCompound(tagString).hasKey("id"))
			return true;
		return false;
	}
	
	private void removeMobSpecifigTags(NBTTagCompound compound)
	{
		compound.removeTag("Pos");
		compound.removeTag("Motion");
		compound.removeTag("Rotation");
		compound.removeTag("UUIDMost");
		compound.removeTag("UUIDLeast");
		//Vanilla-fix incompability
		compound.removeTag("VFAABB");
	}
	
    @Nullable
    public static ResourceLocation getNamedIdFrom(ItemStack stack)
    {
    	if(ItemExtendedSpawnEgg.hasSavedEntity(stack))
    	{
            String s = stack.getTag().getCompound(tagString).getString("id");

            ResourceLocation resourcelocation = new ResourceLocation(s);
            //fixing missing prefix case
            if (!s.contains(":"))
            {
            	stack.getTag().getCompound(tagString).setString("id", resourcelocation.toString());
            }
            return resourcelocation;
    	}
    	return null;
    }
	
	@Override
	public boolean hasEffect(ItemStack stack)
	{
		return ItemExtendedSpawnEgg.hasSavedEntity(stack);
	}
}
