package com.example.pasa_la_pagina.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.pasa_la_pagina.entities.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}

