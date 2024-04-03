package com.example.apiconnect.api

import android.net.http.HttpException
import android.os.Build
import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.annotation.RequiresExtension
import com.example.apiconnect.model.RickAndMorty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class RepositoryImpl(
    private val api: Api //Переменная интерфейса Api
) : Repository {

    //Переопределение метода из interface Repository. Здесь происходит реализация обработки данных
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun getCharacter(): Flow<Result<RickAndMorty>> {
        return flow {
            val rickAndMortyFromApi = try {
                api.getCharacter()
            } catch (e: IOException) {
                e.printStackTrace()
                emit( Result.Error(message = "Error loading products"))
                return@flow
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Result.Error(message = e.message.toString()))
                return@flow
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Result.Error(message = e.message.toString()))
                return@flow
            }

            emit(Result.Success(rickAndMortyFromApi))
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun sendCodeEmail(email: String): Flow<Result<String>> {
        return flow{
            try{
                api.sendCodeEmail(email)
            }
            catch (e: IOException) {
                e.printStackTrace()
                //emit( Result.Error(message = e.message.toString()))
                emit( Result.Error(message = e.message.toString()))
                return@flow
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Result.Error(message = e.message.toString()))
                return@flow
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Result.Error(message = e.message.toString()))
                return@flow
            }
            //emit(Result.Success(request))
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun signIn(email: String, code: String): Flow<Result<String>> {
        return flow{
            try{
                api.signIn(email, code)
            }
            catch (e: IOException) {
                e.printStackTrace()
                //emit( Result.Error(message = e.message.toString()))
                emit( Result.Error(message = "Я код принял"))
                return@flow
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Result.Error(message = "Я код не принял"))
                return@flow
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Result.Error(message = "Я код не принял"))
                return@flow
            }
            //emit(Result.Success(request))
        }
    }

}