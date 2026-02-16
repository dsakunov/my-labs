package com.sakunov.labs.service;

import com.sakunov.labs.model.Character;

import java.util.Hashtable;
import java.util.List;

// Контракт на выполнение операций с персонажами
public interface CharacterService {
    // Метод группировки имен персонажей по gender
    Hashtable<String, List<String>> groupNamesByGender(List<Character> characters);

    // Доп. метод добавления нового персонажа
    void createCharacter(Character character) throws Exception;

    // Доп. метод поиска персонажа по ID
    Character readCharacter(int id) throws Exception;

    // Доп. метод редактирования существующего персонажа
    boolean updateCharacter(Character character) throws Exception;

    // Доп. метод удаления персонажа по ID
    boolean deleteCharacter(int id) throws Exception;
}