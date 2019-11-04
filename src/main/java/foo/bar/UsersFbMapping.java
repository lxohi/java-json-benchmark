package foo.bar;

import com.github.fabienrenaud.jjb.model.Users;
import com.google.flatbuffers.FlatBufferBuilder;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class UsersFbMapping {

    public static byte[] serialize(Users users) {
        FlatBufferBuilder builder = new FlatBufferBuilder(1024);
        int[] userOffsets = new int[users.users.size()];
        for (int i = 0; i < users.users.size(); i++) {
            Users.User user = users.users.get(i);
            int idOffset = builder.createString(user._id);
            int guidOffset = builder.createString(user.guid);
            int balanceOffset = builder.createString(user.balance);
            int pictureOffset = builder.createString(user.picture);
            int eyeColorOffset = builder.createString(user.eyeColor);
            int nameOffset = builder.createString(user.name);
            int genderOffset = builder.createString(user.gender);
            int companyOffset = builder.createString(user.company);
            int emailOffset = builder.createString(user.email);
            int phoneOffset = builder.createString(user.phone);
            int addressOffset = builder.createString(user.address);
            int aboutOffset = builder.createString(user.about);
            int registeredOffset = builder.createString(user.registered);
            int[] tagsOffsets = new int[user.tags.size()];
            for (int j = 0; j < user.tags.size(); j++) {
                tagsOffsets[j] = builder.createString(user.tags.get(j));
            }
            int tagsOffset = UserFb.createTagsVector(builder, tagsOffsets);
            int[] friendOffsets = new int[user.friends.size()];
            for (int j = 0; j < user.friends.size(); j++) {
                Users.Friend f = user.friends.get(j);
                friendOffsets[j] = FriendFb.createFriendFb(builder, builder.createString(f.id), builder.createString(f.name));
            }
            int friendsOffset = UserFb.createFriendsVector(builder, friendOffsets);
            int greetingOffset = builder.createString(user.greeting);
            int favoriteFruitOffset = builder.createString(user.favoriteFruit);

            UserFb.startUserFb(builder);
            UserFb.addId(builder, idOffset);
            UserFb.addIndex(builder, user.index);
            UserFb.addGuid(builder, guidOffset);
            UserFb.addIsActive(builder, user.isActive);
            UserFb.addBalance(builder, balanceOffset);
            UserFb.addPicture(builder, pictureOffset);
            UserFb.addAge(builder, user.age);
            UserFb.addEyeColor(builder, eyeColorOffset);
            UserFb.addName(builder, nameOffset);
            UserFb.addGender(builder, genderOffset);
            UserFb.addCompany(builder, companyOffset);
            UserFb.addEmail(builder, emailOffset);
            UserFb.addPhone(builder, phoneOffset);
            UserFb.addAddress(builder, addressOffset);
            UserFb.addAbout(builder, aboutOffset);
            UserFb.addRegistered(builder, registeredOffset);
            UserFb.addLatitude(builder, user.latitude);
            UserFb.addLongitude(builder, user.longitude);
            UserFb.addTags(builder, tagsOffset);
            UserFb.addFriends(builder, friendsOffset);
            UserFb.addGreeting(builder, greetingOffset);
            UserFb.addFavoriteFruit(builder, favoriteFruitOffset);
            userOffsets[i] = UserFb.endUserFb(builder);
        }
        builder.finish(
                UsersFb.createUsersFb(builder,
                        UsersFb.createUsersVector(builder, userOffsets)));
        return builder.sizedByteArray();
    }

    public static Users deserialize(byte[] bytes) {
        UsersFb usfb = UsersFb.getRootAsUsersFb(ByteBuffer.wrap(bytes));
        Users us = new Users();
        List<Users.User> userList = new ArrayList<>(usfb.usersLength());
        us.users = userList;
        for (int i = 0; i < usfb.usersLength(); i++) {
            UserFb ufb = usfb.users(i);
            Users.User user = new Users.User();
            user._id = ufb.id();
            user.index = ufb.index();
            user.guid = ufb.guid();
            user.isActive = ufb.isActive();
            user.balance = ufb.balance();
            user.picture = ufb.picture();
            user.age = ufb.age();
            user.eyeColor = ufb.eyeColor();
            user.name = ufb.name();
            user.gender = ufb.gender();
            user.company = ufb.company();
            user.email = ufb.email();
            user.phone = ufb.phone();
            user.address = ufb.address();
            user.about = ufb.about();
            user.registered = ufb.registered();
            user.latitude = ufb.latitude();
            user.longitude = ufb.longitude();

            List<String> tagsList = new ArrayList<>(ufb.tagsLength());
            for (int j = 0; j < ufb.tagsLength(); j++) {
                tagsList.add(ufb.tags(j));
            }
            user.tags = tagsList;

            List<Users.Friend> friendsList = new ArrayList<>(ufb.friendsLength());
            for (int j = 0; j < ufb.friendsLength(); j++) {
                FriendFb ffb = ufb.friends(j);
                Users.Friend f = new Users.Friend();
                f.id = ffb.id();
                f.name = ffb.name();
                friendsList.add(f);
            }
            user.friends = friendsList;

            user.greeting = ufb.greeting();
            user.favoriteFruit = ufb.favoriteFruit();
            userList.add(user);
        }
        return us;
    }

}
