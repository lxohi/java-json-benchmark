package com.github.fabienrenaud.jjb;

import com.github.fabienrenaud.jjb.model.Users;
import com.github.fabienrenaud.jjb.support.Api;
import com.github.fabienrenaud.jjb.support.BenchSupport;
import foo.bar.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.File;

import static org.junit.Assert.fail;

/**
 * Created by frenaud on 6/30/16.
 */
public abstract class JsonBenchmarkUsersTest extends JsonBenchmark<Users> {

    private static File configFile;

    public JsonBenchmarkUsersTest(JsonBench BENCH, Api BENCH_API) {
        super(BENCH, BenchSupport.USERS, BENCH_API);
    }

    @BeforeClass
    public static void setUpClass() {
        Cli.AbstractCommand ser = new Cli.SerializationCommand();
        ser.dataType = "users";
        ser.numberOfPayloads = 1;
        ser.sizeOfEachPayloadInKb = 2;
        configFile = Config.save(ser);
    }

    @AfterClass
    public static void tearDownClass() {
        configFile.delete();
    }

    @Override
    protected void testPojo(Users o) {
        Object original = BENCH.JSON_SOURCE().nextPojo();
        if (!o.equals(original)) {
            System.out.println("Difference in Users!");
            System.out.println("   Original   : " + original);
            System.out.println("   Transformed: " + o);
            System.out.println();
            fail();
        }
    }

    @Override
    protected void testProtobuf(User.UsersProto obj) {
        User.UsersProto users = BENCH.JSON_SOURCE().nextProtobuf();
        if (!users.equals(obj)) {
            System.out.println("Difference in Users!");
            System.out.println("   Original   : " + users);
            System.out.println("   Transformed: " + obj);
            System.out.println();
            fail();
        }
    }

    @Override
    protected Class<Users> pojoType() {
        return Users.class;
    }

}
