package com.github.fabienrenaud.jjb;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
import com.github.fabienrenaud.jjb.model.Users;

import java.util.ArrayList;
import java.util.Arrays;

public class KryoUtils {
    private final static ThreadLocal<Kryo> tlKryo = new ThreadLocal<>();

    static {
//        Log.set(0);
    }

    private static Kryo getKryo() {
        Kryo kryo = tlKryo.get();
        if (kryo == null) {
            kryo = new Kryo();
            kryo.register(Users.class);
            kryo.register(Users.User.class);
            kryo.register(ArrayList.class);
            kryo.register(Users.Friend.class);
            kryo.register(String.class);
            tlKryo.set(kryo);
        }
        return kryo;
    }

    public static byte[] serialize(Users users) {
        Output output = new Output(1024, 209715200);
        getKryo().writeObject(output, users);
        return Arrays.copyOfRange(output.getBuffer(), 0, output.position());
    }

    public static Users deserialize(byte[] bytes) {
        Input input = new Input(bytes);
        return getKryo().readObject(input, Users.class);
    }

}
