package com.github.fabienrenaud.jjb;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.fabienrenaud.jjb.model.Users;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class JvmSerializeUtils {

    public static byte[] serialize(Users users) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(users);
            oos.close();
            baos.close();
            return baos.toByteArray();
        } catch (Exception ioe) {
            throw new RuntimeException((ioe));
        }
    }

    public static Users deserialize(byte[] bytes) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            Users u = (Users) ois.readObject();
            ois.close();
            bis.close();
            return u;
        } catch (Exception ioe) {
            throw new RuntimeException((ioe));
        }
    }

}
