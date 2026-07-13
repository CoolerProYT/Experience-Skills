package com.coolerpromc.experienceskills;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {

	public static final String MODID = "experienceskills";
	public static final String MOD_NAME = "Experience Skills";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	public static Identifier id(String path){
		return Identifier.fromNamespaceAndPath(MODID, path);
	}
}