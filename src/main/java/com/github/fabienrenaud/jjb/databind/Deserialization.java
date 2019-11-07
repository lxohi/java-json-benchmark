package com.github.fabienrenaud.jjb.databind;

import com.alibaba.fastjson.JSON;
import com.bluelinelabs.logansquare.LoganSquare;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.github.fabienrenaud.jjb.JsonBench;
import com.github.fabienrenaud.jjb.JvmSerializeUtils;
import com.github.fabienrenaud.jjb.KryoUtils;
import com.github.fabienrenaud.jjb.data.JsonSource;
import com.github.fabienrenaud.jjb.model.Users;
import com.google.gson.JsonSyntaxException;
import foo.bar.UsersFb;
import foo.bar.UsersFbMapping;
import org.openjdk.jmh.annotations.Benchmark;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;


/**
 * @author Fabien Renaud
 */
public class Deserialization extends JsonBench {

    public JsonSource JSON_SOURCE() {
        return CLI_JSON_SOURCE;
    }

    @Benchmark
    @Override
    public Object gson() throws Exception {
        return JSON_SOURCE().provider().gson().fromJson(JSON_SOURCE().nextReader(), JSON_SOURCE().pojoType());
    }

    @Benchmark
    @Override
    public Object jackson() throws Exception {
        return JSON_SOURCE().provider().jackson().readValue(JSON_SOURCE().nextByteArray(), JSON_SOURCE().pojoType());
    }

    @Benchmark
    @Override
    public Object jackson_afterburner() throws IOException {
        return JSON_SOURCE().provider().jacksonAfterburner().readValue(JSON_SOURCE().nextByteArray(), JSON_SOURCE().pojoType());
    }

    @Benchmark
    @Override
    public Object genson() throws JsonSyntaxException {
        return JSON_SOURCE().provider().genson().deserialize(JSON_SOURCE().nextByteArray(), JSON_SOURCE().pojoType());
    }

    @Benchmark
    @Override
    public Object yasson() throws JsonSyntaxException {
        return JSON_SOURCE().provider().yasson().fromJson(JSON_SOURCE().nextReader(), JSON_SOURCE().pojoType());
    }

    @Benchmark
    @Override
    public Object fastjson() {
        return JSON.parseObject(JSON_SOURCE().nextByteArray(), JSON_SOURCE().pojoType());
    }

    @Benchmark
    @Override
    public Object flexjson() throws JsonSyntaxException {
        return JSON_SOURCE().provider().flexjsonDeser().deserialize(JSON_SOURCE().nextReader(), JSON_SOURCE().pojoType());
    }

    @Benchmark
    @Override
    public Object boon() throws Exception {
        return JSON_SOURCE().provider().boon().readValue(JSON_SOURCE().nextByteArray(), JSON_SOURCE().pojoType());
    }

    @Benchmark
    @Override
    public Object johnzon() throws Exception {
        return JSON_SOURCE().provider().johnzon().readObject(JSON_SOURCE().nextInputStream(), JSON_SOURCE().pojoType());
    }

    @Benchmark
    @Override
    public Object jsonsmart() throws Exception {
        return net.minidev.json.JSONValue.parse(JSON_SOURCE().nextByteArray(), JSON_SOURCE().pojoType());
    }

    @Benchmark
    @Override
    public Object dsljson() throws Exception {
        byte[] buffer = JSON_SOURCE().nextByteArray();
        return JSON_SOURCE().provider().dsljson().deserialize(JSON_SOURCE().pojoType(), buffer, buffer.length);
    }

    @Benchmark
    @Override
    public Object dsljson_reflection() throws Exception {
        byte[] buffer = JSON_SOURCE().nextByteArray();
        return JSON_SOURCE().provider().dsljson_reflection().deserialize(JSON_SOURCE().pojoType(), buffer, buffer.length);
    }

    @Benchmark
    @Override
    public Object logansquare() throws Exception {
        return LoganSquare.parse(JSON_SOURCE().nextInputStream(), JSON_SOURCE().pojoType());
    }

    @Benchmark
    @Override
    public Object jodd() throws Exception {
        return JSON_SOURCE().provider().joddDeser().parse(JSON_SOURCE().nextString(), JSON_SOURCE().pojoType());
    }

    @Benchmark
    @Override
    public Object moshi() throws Exception {
        return JSON_SOURCE().provider().moshi().fromJson(JSON_SOURCE().nextOkioBufferedSource());
    }

    @Benchmark
    @Override
    public Object jsoniter() throws Exception {
        return com.jsoniter.JsonIterator.deserialize(JSON_SOURCE().nextByteArray(), JSON_SOURCE().pojoType());
    }

    @Benchmark
    @Override
    public Object protobuf() throws Exception {
        return foo.bar.User.UsersProto.parseFrom(JSON_SOURCE().nextProtoByteArray());
    }

    @Benchmark
    @Override
    public Object flatbuffers() throws Exception {
        return UsersFbMapping.deserialize(JSON_SOURCE().nextFlatbuffersByteArray());
    }

    @Benchmark
    @Override
    public Object flatbuffers_read_through() throws Exception {
        return UsersFbMapping.deserializeThrough(JSON_SOURCE().nextFlatbuffersByteArray());
    }

    @Benchmark
    @Override
    public Object flatbuffers_read_through2() throws Exception {
        return UsersFbMapping.deserializeThrough2(JSON_SOURCE().nextFlatbuffersByteArray());
    }

    private final static Kryo kryo = new Kryo();

    static {
        kryo.register(Users.class);
        kryo.register(Users.User.class);
        kryo.register(ArrayList.class);
        kryo.register(Users.Friend.class);
    }

    @Benchmark
    @Override
    public Object kryo() throws Exception {
        return KryoUtils.deserialize(JSON_SOURCE().nextKryoByteArray());
    }

    @Benchmark
    @Override
    public Object jvm() throws Exception {
        return JvmSerializeUtils.deserialize(JSON_SOURCE().nextSerializableByteArray());
    }

}
