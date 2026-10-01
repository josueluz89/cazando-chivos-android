package com.cazandochivos.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventoDao {
    @Query("SELECT * FROM eventos ORDER BY fecha ASC, hora ASC")
    fun observarTodos(): Flow<List<EventoEntity>>

    @Query("SELECT * FROM eventos WHERE id = :id")
    fun observarPorId(id: String): Flow<EventoEntity?>

    @Query("SELECT * FROM eventos WHERE fecha = :fecha ORDER BY hora ASC")
    suspend fun porFecha(fecha: String): List<EventoEntity>

    @Query("SELECT * FROM eventos WHERE favorito = 1 ORDER BY fecha ASC, hora ASC")
    fun observarFavoritos(): Flow<List<EventoEntity>>

    @Query("SELECT id FROM eventos WHERE favorito = 1")
    suspend fun favoritosIds(): List<String>

    @Query("SELECT COUNT(*) FROM eventos")
    suspend fun contar(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(eventos: List<EventoEntity>)

    @Query("UPDATE eventos SET favorito = :fav WHERE id = :id")
    suspend fun setFavorito(id: String, fav: Boolean)

    @Query("DELETE FROM eventos")
    suspend fun borrarTodos()
}

@Dao
interface LocalDao {
    @Query("SELECT * FROM locales ORDER BY nombre ASC")
    fun observarTodos(): Flow<List<LocalEntity>>

    @Query("SELECT * FROM locales WHERE nombre = :nombre")
    fun observarPorNombre(nombre: String): Flow<LocalEntity?>

    @Query("SELECT * FROM locales WHERE favorito = 1 ORDER BY nombre ASC")
    fun observarFavoritos(): Flow<List<LocalEntity>>

    @Query("SELECT nombre FROM locales WHERE favorito = 1")
    suspend fun favoritosNombres(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(locales: List<LocalEntity>)

    @Query("UPDATE locales SET favorito = :fav WHERE nombre = :nombre")
    suspend fun setFavorito(nombre: String, fav: Boolean)

    @Query("DELETE FROM locales")
    suspend fun borrarTodos()
}

@Dao
interface MetaDao {
    @Query("SELECT * FROM meta WHERE id = 1")
    fun observar(): Flow<MetaEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(meta: MetaEntity)
}
