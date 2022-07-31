public class User
{
    private boolean         missing_info;
    private final String    client_id;
    private String          user_name;
    private UserColour      user_colour;

    public User(final String client_id) {
        this.client_id      = client_id;
        this.user_colour    = new UserColour();
        this.user_name      = client_id;
    }

    public String get_user_name()           { return this.user_name; }
    public String get_user_colour()         { return this.user_colour.toString(); }
    public UserColour get_user_colour_ref() { return this.user_colour; }
    public String get_client_id()           { return this.client_id; }                  // You really shouldn't need this but just in case
    public void set_user_name(String un)    { this.user_name = un; }
}
