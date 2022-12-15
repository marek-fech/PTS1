import DataTypes.HandPosition;
import Enumerations.CardType;
import Interfaces.Position;
import Interfaces.QueenCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EvaluateAttack {
    private CardType defenseCardType;
    private List<Player> playerList;
    private MoveQueen moveQueen;
    private QueenCollection queenCollection;

    public EvaluateAttack(List<Player> playerList, MoveQueen moveQueen){
        this.playerList = playerList;
        this.moveQueen = moveQueen;
    }

    public boolean play(Position targetQueen, int targetPlayerIdx){
        //if player does exist in the game
        if(playerList.size() > targetPlayerIdx && targetPlayerIdx >= 0) {

            Player targetPlayer = playerList.get(targetPlayerIdx);
            Set<Position> playersQueeens = targetPlayer.getAwokenQueens().getQueens().keySet();

            for (Position position : playersQueeens) {
                if (position.getCardIndex() == targetQueen.getCardIndex()) {

                    HandPosition autoDefense = targetPlayer.getHand().hasCardOfType(defenseCardType);

                    //if there are ways to defend
                    if (autoDefense != null) {
                        List<HandPosition> toPick = new ArrayList<>();
                        toPick.add(autoDefense);

                        targetPlayer.getHand().pickCards(toPick);
                        targetPlayer.getHand().removePickedCardsAndRedraw();

                    }
                    //if there are no ways to defend
                    else {
                        moveQueen.setQueenCollection(queenCollection);
                        moveQueen.play(targetQueen);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public void setDefenseCardType(CardType defenseCardType) {
        this.defenseCardType = defenseCardType;
    }

    public void setQueenCollection(QueenCollection queenCollection) {
        this.queenCollection = queenCollection;
    }
}
