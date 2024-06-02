package com.jiunntarn.jsbsp.card.core

import com.jiunntarn.jsbsp.card.core.data.dto.Card
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CardManager {
    private val cardList = mutableSetOf<Card>()

    fun getCardById(cardId: Long): Card? {
        return cardList.firstOrNull { it.cardId == cardId }
    }

    fun getCards(): Set<Card> {
        return cardList
    }

    fun hasCard(): Boolean {
        return cardList.isNotEmpty()
    }

    fun hasAvailableCard(): Boolean {
        return cardList.any { it.isAvailable }
    }

    fun getAvailableCard(): Card? {
        val availableCardList = getAvailableCards()

        return availableCardList.minByOrNull { it.frequency }
    }

    fun getAvailableCards(): List<Card> {
        return cardList.filter { it.isAvailable }
    }

    fun addOrUpdateCard(card: Card) {
        cardList.removeIf { it.cardId == card.cardId }
        cardList.add(card)
    }

    fun removeCard(cardId: Long) {
        cardList.removeIf { it.cardId == cardId }
    }

    /**
     * Return all the cards that satisfy the given predicate.
     *
     * @param remove if true, will remove all the cards that
     * not satisfy the given predicate.
     *
     */
    fun filterBy(predicate: (Card) -> Boolean, remove: Boolean): List<Card> {
        val filteredList = cardList.filter(predicate)
        if (remove) {
            cardList.removeIf { !predicate(it) }
        }
        return filteredList
    }

    fun nextAvailableTime(): LocalDateTime? {
        return cardList.minOfOrNull { it.nextAvailableTime }
    }

    fun validateCard(cardId: Long): Boolean {
        return cardList.firstOrNull { it.cardId == cardId }?.isAvailable == true
    }

    fun clear() {
        cardList.clear()
    }

}