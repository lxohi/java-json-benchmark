package circe.benchmark

import com.github.fabienrenaud.jjb.model.Users
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._, io.circe.generic.extras._
import scala.collection.JavaConverters._

object CirceUtil {

  def serialize(users: ScalaUsers): String = {
    val json = users.asJson.noSpaces
    json
  }

  def deserialize(json: String): ScalaUsers = {
    val users = decode[ScalaUsers](json).right.get
    users
  }

  def usersToScala(users: Users): ScalaUsers = {
    val sus = for {
      user <- users.users.asScala
    } yield {
      val sfs = for {
        friend <- user.friends.asScala
      } yield {
        ScalaFriend(friend.id, friend.name)
      }
      ScalaUser(user._id,
        user.index,
        user.guid,
        user.isActive,
        user.balance,
        user.picture,
        user.age,
        user.eyeColor,
        user.name,
        user.gender,
        user.company,
        user.email,
        user.phone,
        user.address,
        user.about,
        user.registered,
        user.latitude,
        user.longitude,
        user.tags.asScala,
        sfs,
        user.greeting,
        user.favoriteFruit)
    }
    ScalaUsers(sus)
  }

}

case class ScalaUsers(users: Seq[ScalaUser])

import io.circe.generic.extras.defaults._

@ConfiguredJsonCodec case class ScalaUser(@JsonKey("_id") id: String,
                                          index: Int,
                                          guid: String,
                                          isActive: Boolean,
                                          balance: String,
                                          picture: String,
                                          age: Int,
                                          eyeColor: String,
                                          name: String,
                                          gender: String,
                                          company: String,
                                          email: String,
                                          phone: String,
                                          address: String,
                                          about: String,
                                          registered: String,
                                          latitude: Double,
                                          longitude: Double,
                                          tags: Seq[String],
                                          friends: Seq[ScalaFriend],
                                          greeting: String,
                                          favoriteFruit: String)

case class ScalaFriend(id: String, name: String)
