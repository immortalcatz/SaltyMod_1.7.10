package ru.liahim.saltmod.world;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import ru.liahim.saltmod.init.ModBlocks;
import ru.liahim.saltmod.init.SaltConfig;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.Loader;

public class SaltCrystalGenerator implements IWorldGenerator {

	int dimensionIDTwilightForest = 0;

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (Loader.isModLoaded("TwilightForest")) {
			dimensionIDTwilightForest = SaltConfig.TFDim;
		}
		if (world.provider.dimensionId == 0 || (world.provider.dimensionId == dimensionIDTwilightForest && SaltConfig.TFOreGen)) {
			generateOverworld(world, random, chunkX * 16, chunkZ * 16);
		}
	}

	public void generateOverworld(World world, Random rand, int x, int z) {
		int dimensionSpawnHeight = 40;
		if (world.provider.dimensionId == 0) {
			dimensionSpawnHeight = 40;
		} else if (world.provider.dimensionId == dimensionIDTwilightForest) {
			dimensionSpawnHeight = 20;
		}

		for (int y = 8; y < dimensionSpawnHeight; y++) {
			for (int x1 = x; x1 < x + 16; x1++) {
				for (int z1 = z; z1 < z + 16; z1++) {

					if (world.getBlock(x1, y - 1, z1) == ModBlocks.saltOre && world.isAirBlock(x1, y, z1) && world.getFullBlockLightValue(x1, y, z1) < 13) {
						if (rand.nextInt(2) == 0) {
							world.setBlock(x1, y, z1, ModBlocks.saltCrystal, 1, 3);
						} else {
							world.setBlock(x1, y, z1, ModBlocks.saltCrystal);
						}
					}
				}
			}
		}
	}
}