package com.example.minilivescore.data.repository

import android.app.Activity
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.minilivescore.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val credentialManager: CredentialManager
) {
     fun login(email:String,password:String):Flow<Result<FirebaseUser>>{
        return flow {
            try {
                val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                emit(Result.success(authResult.user!!))
            }catch (e:Exception){
                emit(Result.failure(e))
            }
        }.flowOn(Dispatchers.IO)
     }
    fun loginWithGoogle(activity: Activity):Flow<Result<FirebaseUser>>{
        return flow {
            try {
                //Cấu hình google sign in
                val googleIdOption:GetGoogleIdOption = GetGoogleIdOption.Builder()
                    .setServerClientId(activity.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .setAutoSelectEnabled(false)
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                //Lấy credential
                val result = credentialManager.getCredential(activity,request)
                //Xử lý GoogleIDtoken
                val credential = result.credential
                Log.d("AuthResult", "Result: ${result.credential}")
                if(credential is CustomCredential
                    && credential.type ==GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    //Xác thực với firebase
                    val firebaseCredential = GoogleAuthProvider.getCredential(
                        googleIdTokenCredential.idToken,
                        null
                    )
                    val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
                    emit(Result.success(authResult.user!!))
                }else{
                    emit(Result.failure(Exception("Không đúng xác thực")))
                }
            }catch (e:Exception){
                emit(Result.failure(e))
            }
        }.flowOn(Dispatchers.IO)
    }
    fun getCurrentUser():FirebaseUser?{
        return firebaseAuth.currentUser
    }
    fun signOut() = firebaseAuth.signOut()
}
