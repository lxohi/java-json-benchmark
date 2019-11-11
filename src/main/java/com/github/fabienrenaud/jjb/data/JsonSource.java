package com.github.fabienrenaud.jjb.data;

import circe.benchmark.CirceUtil;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.github.fabienrenaud.jjb.JvmSerializeUtils;
import com.github.fabienrenaud.jjb.KryoUtils;
import com.github.fabienrenaud.jjb.RandomUtils;
import com.github.fabienrenaud.jjb.data.gen.DataGenerator;
import com.github.fabienrenaud.jjb.model.Users;
import com.github.fabienrenaud.jjb.provider.JsonProvider;
import com.github.fabienrenaud.jjb.stream.StreamDeserializer;
import com.github.fabienrenaud.jjb.stream.StreamSerializer;
import com.google.flatbuffers.FlatBufferBuilder;
import foo.bar.User;
import foo.bar.UsersFb;
import foo.bar.UsersFbMapping;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by frenaud on 7/23/16.
 */
public abstract class JsonSource<T> {

    static {
        JsonSourceInits.jsoniter();
    }

    private final JsonProvider<T> provider;

    private final T[] jsonAsObject;
    private final Object[] jsonAsScalaObject;
    private final User.UsersProto[] protobufAsObject;
    private final String[] jsonAsString;
    private final byte[][] jsonAsBytes;
    private final byte[][] protobufAsBytes;
    private final byte[][] flatbuffersAsBytes;
    private final byte[][] kryoAsBytes;
    private final byte[][] serializableAsBytes;
    private final ThreadLocal<ByteArrayInputStream[]> jsonAsByteArrayInputStream;

    private final DataGenerator<T> dataGenerator;
    private final StreamSerializer<T> streamSerializer;
    private final StreamDeserializer<T> streamDeserializer;

    JsonSource(final int quantity, final int individualSize, final JsonProvider provider, final DataGenerator<T> dataGenerator, final StreamSerializer<T> streamSerializer, final StreamDeserializer<T> streamDeserializer) {
        this.provider = provider;

        this.jsonAsObject = newPojoArray(quantity);
        this.jsonAsScalaObject = new Object[quantity];
        this.protobufAsObject = new User.UsersProto[quantity];
        this.jsonAsString = new String[quantity];
        this.jsonAsBytes = new byte[quantity][];
        this.protobufAsBytes = new byte[quantity][];
        this.flatbuffersAsBytes = new byte[quantity][];
        this.kryoAsBytes = new byte[quantity][];
        this.serializableAsBytes = new byte[quantity][];

        this.dataGenerator = dataGenerator;
        this.streamSerializer = streamSerializer;
        this.streamDeserializer = streamDeserializer;
        populateFields(quantity, individualSize);

        this.jsonAsByteArrayInputStream = ThreadLocal.withInitial(() -> {
            ByteArrayInputStream[] arr = new ByteArrayInputStream[quantity];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = new ByteArrayInputStream(jsonAsBytes[i]);
            }
            return arr;
        });
    }

    private final void populateFields(final int quantity, final int individualSize) {
        try {
            for (int i = 0; i < quantity; i++) {
                T obj = pojoType().newInstance();
                dataGenerator.populate(obj, individualSize);
                jsonAsObject[i] = obj;

                String json = provider.jackson().writeValueAsString(obj);
                jsonAsString[i] = json;
                jsonAsBytes[i] = json.getBytes();

                if (obj.getClass() == Users.class) {
                    Users users = (Users) obj;

                    // protobuf
                    foo.bar.User.UsersProto.Builder usersBuilder = foo.bar.User.UsersProto.newBuilder();
                    for (Users.User user : users.users) {
                        foo.bar.User.UserProto.Builder userBuilder = foo.bar.User.UserProto.newBuilder();
                        userBuilder.setId(user._id);
                        userBuilder.setIndex(user.index);
                        userBuilder.setGuid(user.guid);
                        userBuilder.setIsActive(user.isActive);
                        userBuilder.setBalance(user.balance);
                        userBuilder.setPicture(user.picture);
                        userBuilder.setAge(user.age);
                        userBuilder.setEyeColor(user.eyeColor);
                        userBuilder.setName(user.name);
                        userBuilder.setGender(user.gender);
                        userBuilder.setCompany(user.company);
                        userBuilder.setEmail(user.email);
                        userBuilder.setPhone(user.phone);
                        userBuilder.setAddress(user.address);
                        userBuilder.setAbout(user.about);
                        userBuilder.setRegistered(user.registered);
                        userBuilder.setLatitude(user.latitude);
                        userBuilder.setLongitude(user.longitude);
                        userBuilder.addAllTags(user.tags);
                        userBuilder.addAllFriends(user.friends.stream().map(v -> {
                            foo.bar.User.FriendProto.Builder friendBuilder = foo.bar.User.FriendProto.newBuilder();
                            friendBuilder.setId(v.id);
                            friendBuilder.setName(v.name);
                            return friendBuilder.build();
                        }).collect(Collectors.toList()));
                        userBuilder.setGreeting(user.greeting);
                        userBuilder.setFavoriteFruit(user.favoriteFruit);
                        usersBuilder.addUsers(userBuilder);
                    }
                    protobufAsObject[i] = usersBuilder.build();
                    protobufAsBytes[i] = protobufAsObject[i].toByteArray();

                    // flatbuffers
                    flatbuffersAsBytes[i] = UsersFbMapping.serialize(users);

                    // kryo
                    kryoAsBytes[i] = KryoUtils.serialize(users);

                    // serializable
                    serializableAsBytes[i] = JvmSerializeUtils.serialize(users);

                    // scala object
                    jsonAsScalaObject[i] = CirceUtil.usersToScala(users);

                    System.out.println("======= json size: " + jsonAsBytes[i].length + ", protobuf size: " + protobufAsBytes[i].length + ", flatbuffers size: " + flatbuffersAsBytes);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public JsonProvider<T> provider() {
        return provider;
    }

    public String nextString() {
        return jsonAsString[index(jsonAsString.length)];
    }

    public InputStream nextInputStream() {
        ByteArrayInputStream[] arr = jsonAsByteArrayInputStream.get();
        ByteArrayInputStream bais = arr[index(arr.length)];
        bais.reset();
        return bais;
    }

    public byte[] nextByteArray() {
        return jsonAsBytes[index(jsonAsBytes.length)];
    }

    public byte[] nextProtoByteArray() {
        return protobufAsBytes[index(jsonAsBytes.length)];
    }

    public byte[] nextFlatbuffersByteArray() {
        return flatbuffersAsBytes[index(jsonAsBytes.length)];
    }

    public byte[] nextKryoByteArray() {
        return kryoAsBytes[index(jsonAsBytes.length)];
    }

    public byte[] nextSerializableByteArray() {
        return serializableAsBytes[index(jsonAsBytes.length)];
    }

    public Reader nextReader() {
        return new InputStreamReader(nextInputStream());
    }

    public BufferedSource nextOkioBufferedSource() {
        return Okio.buffer(new ForwardingSource(Okio.source(nextInputStream())) {
            @Override
            public void close() {
            }
        });
    }

    public T nextPojo() {
        return jsonAsObject[index(jsonAsObject.length)];
    }

    public Object nextScalaObject() {
        return jsonAsScalaObject[index(jsonAsObject.length)];
    }

    public User.UsersProto nextProtobuf() {
        return protobufAsObject[index(jsonAsObject.length)];
    }

    public StreamSerializer<T> streamSerializer() {
        return streamSerializer;
    }

    public StreamDeserializer<T> streamDeserializer() {
        return streamDeserializer;
    }

    abstract T[] newPojoArray(int quantity);

    public abstract Class<T> pojoType();

    private int index(final int bound) {
        return bound == 1 ? 0 : RandomUtils.nextInt(bound);
    }
}
