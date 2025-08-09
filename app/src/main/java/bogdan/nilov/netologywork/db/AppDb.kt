package bogdan.nilov.netologywork.db

import androidx.room.Database
import androidx.room.RoomDatabase
import bogdan.nilov.netologywork.dao.EventDao
import bogdan.nilov.netologywork.dao.EventRemoteKeyDao
import bogdan.nilov.netologywork.dao.PostDao
import bogdan.nilov.netologywork.dao.PostRemoteKeyDao
import bogdan.nilov.netologywork.dao.UserDao
import bogdan.nilov.netologywork.entity.EventEntity
import bogdan.nilov.netologywork.entity.EventRemoteKeyEntity
import bogdan.nilov.netologywork.entity.PostEntity
import bogdan.nilov.netologywork.entity.PostRemoteKeyEntity
import bogdan.nilov.netologywork.entity.UserEntity


@Database(
    entities = [
        PostEntity::class,
        PostRemoteKeyEntity::class,
        EventEntity::class,
        EventRemoteKeyEntity::class,
        UserEntity::class,
    ], version = 5,
    exportSchema = false

)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun eventDao(): EventDao
    abstract fun eventRemoteKeyDao(): EventRemoteKeyDao
    abstract fun userDao(): UserDao
}