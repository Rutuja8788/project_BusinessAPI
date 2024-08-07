package com.example.welcome.repository;



import com.example.welcome.entity.IncomingMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface IncomingMessageRepository extends JpaRepository<IncomingMessage, Long> {
}

