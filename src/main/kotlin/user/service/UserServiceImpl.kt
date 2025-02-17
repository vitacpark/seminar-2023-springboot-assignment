package com.wafflestudio.seminar.spring2023.user.service

import com.wafflestudio.seminar.spring2023.user.repository.UserEntity
import com.wafflestudio.seminar.spring2023.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
        private val userRepository: UserRepository,
) : UserService {
    override fun signUp(username: String, password: String, image: String): User {
        if(username.length < 4) throw SignUpBadUsernameException()
        if(password.length < 4) throw SignUpBadPasswordException()
        if(userRepository.existsByUsername(username)) throw SignUpUsernameConflictException()
        userRepository.save(UserEntity(username = username, password = password, image = image))
        return User(username = username,image=image)
    }

    override fun signIn(username: String, password: String): User {
        if(!userRepository.existsByUsername(username)) throw SignInUserNotFoundException()
        if(!userRepository.existsByUsernameAndPassword(username,password)) throw SignInInvalidPasswordException()
        val curUser = userRepository.findByUsername(username)
        return User(username = curUser.username,image = curUser.image)
    }

    override fun authenticate(accessToken: String): User {
        val users = userRepository.findAll().map{User(it.username,it.image)}
        for(user in users){
            if(user.getAccessToken() == accessToken) return user
        }
        throw AuthenticateException()
    }
}