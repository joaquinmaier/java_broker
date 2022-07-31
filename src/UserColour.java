import java.util.Random;

// ? Simple class designed to facilitate the ANSI escaping for colouring names
public class UserColour
{
    private boolean     light_colour;
    private Integer     colour;

    public UserColour() {
        Random  rand = new Random();

        this.light_colour   = rand.nextBoolean();
        this.colour         = rand.nextInt(8) + 30;     // ANSI colouring goes from 0;30 to 0;37
    }

    public UserColour(boolean lc, int c) {
        this.light_colour = lc;
        this.colour = c;
    }

    public final boolean get_light_colour()     { return light_colour; }
    public void set_light_colour(boolean lc)    { this.light_colour = lc; }
    public final Integer get_colour_value()     { return colour; }
    public void set_colour_value(int c)         { this.colour = c; }

    public static String clear() {
        return "\033[0m";
    }

    public String get_colour() {
        return "\033[" + light_colour ? 1 : 0 + ";" + colour + "m";
    }

    @Override
    public String toString() {
        return "\033[" + light_colour ? 1 : 0 + ";" + colour + "m";
    }
}
