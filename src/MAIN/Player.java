package MAIN;

import MAIN.DataTypes.*;
import MAIN.Enumerations.CardType;
import MAIN.Interfaces.HandInterface;
import MAIN.Interfaces.MoveQueenInterface;
import MAIN.Interfaces.PlayerInterface;
import MAIN.Interfaces.Position;

import java.util.*;

public class Player implements PlayerInterface {
    private PlayerState playerState;
    private final HandInterface hand;
    private final int playerIdx;
    private final AwokenQueens awokenQueens;
    private final SleepingQueens sleepingQueens;
    private EvaluateAttack evaluateAttack;
    private MoveQueenInterface moveQueen;


    public Player(HandInterface hand, int playerIdx, SleepingQueens sleepingQueens){
        this.hand = hand;
        this.playerIdx = playerIdx;
        this.awokenQueens = new AwokenQueens(playerIdx);
        this.sleepingQueens = sleepingQueens;
        this.playerState = new PlayerState();

        updatePlayerState();

    }

    private void updatePlayerState(){
        playerState = new PlayerState();

        Map<Integer, Optional<Card>> cardsInHand = new LinkedHashMap<>();
        for(int i = 0; i < hand.getCards().size(); i++){
            cardsInHand.put(i, Optional.of(hand.getCards().get(i)));
        }
        playerState.setCards(cardsInHand);

        Map<Integer, Queen> queenMap = new LinkedHashMap<>();
        for(Position position : awokenQueens.getQueens().keySet()){
            queenMap.put(position.getCardIndex(), awokenQueens.getQueens().get(position));
        }
        playerState.setAwokenQueens(queenMap);
    }

    @Override
    public boolean play(List<Position> cards){
        if(cards.isEmpty() ||
                !(cards.get(0) instanceof HandPosition) ||
                cards.get(0).getPlayerIndex() != playerIdx)
            return false;

        List<HandPosition> handPositionList = new ArrayList<>();

        if(cards.size() == 1){
            Position card = cards.get(0);

            if(hand.getCards().get(card.getCardIndex()).getType() == CardType.Number)
                handPositionList.add((HandPosition) card);
            else return false;

        }
        else if(cards.size() == 2){

            Card cardOne = hand.getCards().get(cards.get(0).getCardIndex());
            CardType type = cardOne.getType();

            if(type == CardType.King){
                if(!(cards.get(1) instanceof SleepingQueenPosition))
                    return false;

                moveQueen.setQueenCollection(awokenQueens);
                if(!moveQueen.play(cards.get(1)))
                    return false;

                handPositionList.add((HandPosition) cards.get(0));
            }
            else if(type == CardType.Knight){
                if(!(cards.get(1) instanceof AwokenQueenPosition) ||
                        cards.get(1).getPlayerIndex() != playerIdx)
                    return false;

                evaluateAttack.setQueenCollection(awokenQueens);
                evaluateAttack.setDefenseCardType(CardType.Dragon);
                if(!evaluateAttack.play(cards.get(1), cards.get(1).getPlayerIndex()))
                    return false;

                handPositionList.add((HandPosition) cards.get(0));
            }
            else if(type == CardType.SleepingPotion){
                if(!(cards.get(1) instanceof AwokenQueenPosition) ||
                        cards.get(1).getPlayerIndex() != playerIdx)
                    return false;

                evaluateAttack.setQueenCollection(sleepingQueens);
                evaluateAttack.setDefenseCardType(CardType.MagicWand);
                if(!evaluateAttack.play(cards.get(1), cards.get(1).getPlayerIndex()))
                    return false;

                handPositionList.add((HandPosition) cards.get(0));
            }
            else{
                if(!(cards.get(1) instanceof HandPosition))
                    return false;

                Card cardTwo = hand.getCards().get(cards.get(1).getCardIndex());
                if(cardOne.getType() != CardType.Number || cardTwo.getType() != CardType.Number)
                    return false;

                EvaluateNumberedCards evaluateNumberedCards = new EvaluateNumberedCards();
                List<Card> cardsList = new ArrayList<>();

                cardsList.add(cardOne);
                cardsList.add(cardTwo);

                if(!evaluateNumberedCards.play(cardsList))
                    return false;

                handPositionList.add((HandPosition) cards.get(0));
                handPositionList.add((HandPosition) cards.get(1));

            }
        }
        else{
            ArrayList<Card> numberedCards = new ArrayList<>();
            ArrayList<Integer> cardIndex = new ArrayList<>();

            for(Position position : cards){
                if(!(position instanceof HandPosition) || position.getPlayerIndex() != playerIdx)
                    return false;

                Card card = hand.getCards().get(position.getCardIndex());
                if(!cardIndex.contains(position.getCardIndex())){
                    if(card.getType() != CardType.Number)
                        return false;

                    numberedCards.add(card);
                    handPositionList.add((HandPosition) position);
                    cardIndex.add(position.getCardIndex());
                }
            }

            EvaluateNumberedCards evaluateNumberedCards = new EvaluateNumberedCards();
            if(!evaluateNumberedCards.play(numberedCards)){
                return false;
            }
        }

        hand.pickCards(handPositionList);
        hand.removePickedCardsAndRedraw();
        updatePlayerState();
        return true;
    }

    //setters
    @Override
    public void setMoveQueen(MoveQueenInterface moveQueen) {
        this.moveQueen = moveQueen;
    }

    @Override
    public void setEvaluateAttack(EvaluateAttack evaluateAttack) {
        this.evaluateAttack = evaluateAttack;
    }

    //getters
    @Override
    public PlayerState getPlayerState() {
        return playerState;
    }

    @Override
    public AwokenQueens getAwokenQueens() {
        return awokenQueens;
    }

    @Override
    public HandInterface getHand() {
        return hand;
    }

    @Override
    public int getPlayerIdx() {
        return playerIdx;
    }
}
