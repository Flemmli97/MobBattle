package com.flemmli97.mobbattle;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;

public class Config {

	public static Configuration config;
	public static boolean showTeamParticles = true;
	public static boolean autoAddAI = true;
	
	public static void load()
	{
		if(config==null)
		{
			config = new Configuration(new File(Loader.instance().getConfigDir(), "mobbattle.cfg"));
			config.load();
		}
		showTeamParticles = config.getBoolean("Show Team Particles", "general", showTeamParticles, "");
		autoAddAI = config.getBoolean("Auto add ai to team mobs", "general", autoAddAI, "Auto target mobs from other teams (if e.g. done per command)");
		config.save();
	}
}
