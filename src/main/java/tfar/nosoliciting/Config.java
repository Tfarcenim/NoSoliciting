package tfar.nosoliciting;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {

  public static ForgeConfigSpec.IntValue range;

  public static final Config SERVER;
  public static final ForgeConfigSpec SERVER_SPEC;

  static {
    final Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config::new);
    SERVER_SPEC = specPair.getRight();
    SERVER = specPair.getLeft();
  }

  Config(ForgeConfigSpec.Builder builder) {
    builder.push("general");

    range = builder
            .comment("Range of effect in chunks, 1 = 3x3, 2 = 5x5, 3 = 7x7,etc")
            .translation("text.nosoliciting.config.range")
            .defineInRange("Chunk Range",2,0,Integer.MAX_VALUE);
    builder.pop();
  }
}
