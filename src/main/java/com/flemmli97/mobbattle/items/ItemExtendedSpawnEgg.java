package com.flemmli97.mobbattle.items;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.items.entitymanager.Team;

import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemExtendedSpawnEgg extends Item{
	
	private static final String tagString = MobBattle.MODID + ":Entity";
	public ItemExtendedSpawnEgg()
	{
		super();
        this.setUnlocalizedName("egg_ex");
        this.setCreativeTab(MobBattle.customTab);
        this.setRegistryName(new ResourceLocation(MobBattle.MODID, "egg_ex"));
    }
	
	//@Override
    //public String getItemStackDisplayName(ItemStack stack)
    //{
    //    return I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name").trim();
    //}
    
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> list, ITooltipFlag flagIn) {
		list.add(TextFormatting.AQUA + "Left click an entity to save it. Shift while doing saves nbt too.");
		if(ItemExtendedSpawnEgg.hasSavedEntity(stack))
		{
			String entity = EntityList.getTranslationName(new ResourceLocation(stack.getTagCompound().getCompoundTag(tagString).getString("id")));
			if(entity!=null)
				list.add(TextFormatting.GOLD + "Spawns "+I18n.format("entity." + entity +".name"));
		}
	}
	
	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		if(entity instanceof EntityLiving)
		{
			EntityLiving e = (EntityLiving) entity;
			NBTTagCompound compound = stack.getTagCompound();
			if(compound==null)
				compound = new NBTTagCompound();
			NBTTagCompound tag = new NBTTagCompound();
			if(player.isSneaking())
				e.writeToNBTAtomically(tag);
			else
			{
		        ResourceLocation name = EntityList.getKey(e);
		        if(name!=null)
		            tag.setString("id", name.toString());
			}
			compound.setTag(tagString, tag);
			stack.setTagCompound(compound);

			if (!player.world.isRemote)
			{
				player.sendMessage(new TextComponentString(TextFormatting.GOLD + "Saved Entity"));
			}
		}
	    return true;
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack itemstack = player.getHeldItem(hand);

        if (world.isRemote)
        {
            return EnumActionResult.SUCCESS;
        }
        else if (!player.canPlayerEdit(pos.offset(facing), facing, itemstack))
        {
            return EnumActionResult.FAIL;
        }
        else
        {
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

            BlockPos blockpos = pos.offset(facing);
            double d0 = this.getYOffset(world, blockpos);
            Entity entity = this.spawnEntity(world, itemstack, blockpos.getX() + 0.5D, blockpos.getY() + d0, blockpos.getZ() + 0.5D);

            if (entity != null)
            {
                if (!player.capabilities.isCreativeMode)
                {
                    itemstack.shrink(1);
                }
                this.applyEntityNBT(entity, itemstack);
            }

            return EnumActionResult.SUCCESS;
        }
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack itemstack = player.getHeldItem(hand);

        if (world.isRemote || !itemstack.hasTagCompound() || !itemstack.getTagCompound().hasKey(MobBattle.MODID + ":Entity"))
        {
            return new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack);
        }
        else
        {
            RayTraceResult raytraceresult = this.rayTrace(world, player, true);
            
            if (raytraceresult != null && raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                BlockPos blockpos = raytraceresult.getBlockPos();

                if (!(world.getBlockState(blockpos).getBlock() instanceof BlockLiquid))
                {
                    return new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack);
                }
                else if (world.isBlockModifiable(player, blockpos) && player.canPlayerEdit(blockpos, raytraceresult.sideHit, itemstack))
                {
                    Entity entity = this.spawnEntity(world, itemstack, blockpos.getX() + 0.5D, blockpos.getY() + 0.5D, blockpos.getZ() + 0.5D);
                    if (entity == null)
                    {
                        return new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack);
                    }
                    else
                    {
                        if (!player.capabilities.isCreativeMode)
                        {
                            itemstack.shrink(1);
                        }
                        this.applyEntityNBT(entity, itemstack);
                        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
                    }
                }
                else
                {
                    return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
                }
            }
            else
            {
                return new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack);
            }
        }
	}
	
    protected double getYOffset(World world, BlockPos pos)
    {
        AxisAlignedBB axisalignedbb = (new AxisAlignedBB(pos)).expand(0.0D, -1.0D, 0.0D);
        List<AxisAlignedBB> list = world.getCollisionBoxes(null, axisalignedbb);

        if (list.isEmpty())
        {
            return 0.0D;
        }
        else
        {
            double d0 = axisalignedbb.minY;

            for (AxisAlignedBB axisalignedbb1 : list)
            {
                d0 = Math.max(axisalignedbb1.maxY, d0);
            }

            return d0 - pos.getY();
        }
    }
	
	private Entity spawnEntity(World world, ItemStack stack, double x, double y, double z)
	{
		Entity entity = null;
		if(ItemExtendedSpawnEgg.hasSavedEntity(stack))
		{
			entity = EntityList.createEntityByIDFromName(new ResourceLocation(stack.getTagCompound().getCompoundTag(tagString).getString("id")), world);
	        if (entity instanceof EntityLiving)
	        {
	            EntityLiving entityliving = (EntityLiving)entity;
	            entity.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
	            entityliving.rotationYawHead = entityliving.rotationYaw;
	            entityliving.renderYawOffset = entityliving.rotationYaw;
	            entityliving.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entityliving)), (IEntityLivingData)null);
	            world.spawnEntity(entity);
	            entityliving.playLivingSound();
	        }
		}
        return entity;
	}
	
	private static boolean hasSavedEntity(ItemStack stack)
	{
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey(tagString) && stack.getTagCompound().getCompoundTag(tagString).hasKey("id"))
			return true;
		return false;
	}
	
	private void applyEntityNBT(Entity e, ItemStack stack)
	{
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey(tagString))
		{
			NBTTagCompound compound = stack.getTagCompound().getCompoundTag(tagString);
			UUID uuid = e.getUniqueID();
			compound.removeTag("Pos");
			compound.removeTag("Motion");
			compound.removeTag("Rotation");
			NBTTagCompound nbt = e.writeToNBT(new NBTTagCompound());            
            nbt.merge(compound);
            e.readFromNBT(nbt);
            e.setUniqueId(uuid);
		}
		if(stack.hasDisplayName() && e instanceof EntityCreature)
		{
            Team.updateEntity(stack.getDisplayName(), (EntityCreature) e);
		}
	}
	
    @Nullable
    public static ResourceLocation getNamedIdFrom(ItemStack stack)
    {
    	if(ItemExtendedSpawnEgg.hasSavedEntity(stack))
    	{
            String s = stack.getTagCompound().getCompoundTag(tagString).getString("id");

            ResourceLocation resourcelocation = new ResourceLocation(s);
            //fixing missing prefix case
            if (!s.contains(":"))
            {
            	stack.getTagCompound().getCompoundTag(tagString).setString("id", resourcelocation.toString());
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

	@SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(Items.SPAWN_EGG.getRegistryName(), "inventory"));
    }
}
