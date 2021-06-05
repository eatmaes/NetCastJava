import NetCastJava.*;
import java.io.IOException;
import java.util.Arrays;

public class Example {
    public static void main(String[] args) throws IOException {
        NetCastClient client = new NetCastClient("192.168.0.169", "694206");
        client.send_command(LG_Commands.VOLUME_UP);
        client.send_command(LG_Commands.VOLUME_DOWN);
        System.out.println(client.get_query_data(LG_Queries.VOLUME_INFO));
        System.out.println(Arrays.toString(client.get_volume_info()));

    }
}
