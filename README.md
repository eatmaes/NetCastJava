# NetCastJava
NetCast client for java

My best try of converting https://github.com/wokar/pylgnetcast into Java 

A java library and command line tool (Not yet) to control LG Smart TV running NetCast 3.0 (LG Smart TV models released in 2012) and NetCast 4.0 (LG Smart TV models released in 2013) via TCP/IP.

## Dependencies
* java 11

## API Usage
```java
import NetCastJava.*;
import java.io.IOException;
import java.util.Arrays;

public class Example {
    public static void main(String... args) throws IOException {
        NetCastClient client = new NetCastClient("192.168.0.169", "694206");
        client.send_command(LG_Commands.VOLUME_UP);
        client.send_command(LG_Commands.VOLUME_DOWN);
        System.out.println(client.get_query_data(LG_Queries.VOLUME_INFO));
        System.out.println(Arrays.toString(client.get_volume_info()));

    }
}

```

## Not A Command Line Tool Yet



