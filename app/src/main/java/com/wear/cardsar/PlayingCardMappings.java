package com.wear.cardsar;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import java.util.LinkedList;
import java.util.List;

import static android.os.SystemClock.sleep;

/**
 * Created by larsoe4 on 4/18/2018.
 */

public class PlayingCardMappings {
    private final CardMapping[] mPlayingCards = new CardMapping[52];
    private final int[] mUnusedPlayingCards = new int[52];
    private int nPlayingCards;
    private int nUniqueCards;
    private int lastAvailablePCard = 0;

    public static final String[] playingCardSuits = {"Clubs", "Diamonds", "Hearts", "Spades"};
    public static final String[] playingCardRanks = {"Ace", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Jack", "King", "Queen"};

    public static String getPlayingCardName(int cardID){
        int suit = cardID / 13;
        int rank = cardID % 13;

        return getRankName(rank) + " of " + getSuitName(suit);
    }

    public static String getSuitName(int suitID){
        return playingCardSuits[suitID];
    }

    public static String getRankName(int rankID){
        return playingCardRanks[rankID];
    }

    public PlayingCardMappings(Game game, AppRepository repository){
        mapToPlayingCards(game, repository);
    }

    public int getnPlayingCards(){
        return nPlayingCards;
    }

    public int getnUniqueCards(){
        return nUniqueCards;
    }

    public CardMapping lookupPlayingCard(int cardID){
        return mPlayingCards[cardID];
    }

    public List<Pair<Integer, Integer>> listUnusedCards(){
        List<Pair<Integer, Integer>> unused = new LinkedList<>();

        for (int i = 0; i < 52; i++){
            if (mUnusedPlayingCards[i] > 0){
                unused.add(new Pair<>(i, mUnusedPlayingCards[i]));
            }
        }

        return unused;
    }

    public List<Integer> listUnusedSuits(){
        List<Integer> unusedSuits = new LinkedList<Integer>();

        boolean suitIsUsed = false;
        for (int i = 0; i < 52; i++){
            if (i > 0 && i % 13 == 0){
                if (!suitIsUsed){
                    unusedSuits.add((i / 13) - 1);
                }
                suitIsUsed = false;
            }

            if (mPlayingCards[i] != null){
                suitIsUsed = true;
            }
        }
        if (!suitIsUsed){
            unusedSuits.add(3);
        }

        return unusedSuits;
    }

    public List<Integer> listUnusedRanks(){
        List<Integer> unusedRanks = new LinkedList<Integer>();

        for (int rank = 0; rank < 13; rank++){
            boolean rankUsed = false;
            for (int suit = 0; suit < 4; suit++){
                if (mPlayingCards[(suit * 13) + rank] != null){
                    rankUsed = true;
                }
            }
            if (!rankUsed){
                unusedRanks.add(rank);
            }
        }

        return unusedRanks;
    }

    public void printMappings(){
        for (int i = 0; i < 52; i++){
            if (mPlayingCards[i] != null){
                Log.d("Playing cards", getPlayingCardName(i) + " to " + mPlayingCards[i].getMappingName());
            }
        }
    }


    private static List<CardMapping> loadMappings(Game game, AppRepository repository){


        return repository.findStaticMappingsByName(game.getGameName());
    }

    private void countCards(List<CardMapping> cardMappings){

        nPlayingCards = 0;
        nUniqueCards = 0;
        for (CardMapping mapping : cardMappings){
            nUniqueCards++;
            nPlayingCards += mapping.getQuantity();
        }

        Log.d("nUniqueCards", String.valueOf(nUniqueCards));
        Log.d("nPlayingCards", String.valueOf(nPlayingCards));
    }


    private void mapToPlayingCards(Game game, AppRepository repository){
        List<CardMapping> mappings = loadMappings(game, repository);

        countCards(mappings);

        int nDecks = (nPlayingCards / 52) + 1;

        int currentPCard = 0;
        for (CardMapping mapping : mappings){
            int cardsToAlloc = mapping.getQuantity();
            while (cardsToAlloc > 0){
                mPlayingCards[currentPCard] = mapping;
                currentPCard++;

                // after mapping to a
                cardsToAlloc -= nDecks;
            }

            // Each unique card is mapped to a playing card
            // If there are multiple decks, and not enough of unique mapping,
            // extra cards from other decks must be removed
            if (cardsToAlloc < 0){
                mUnusedPlayingCards[currentPCard - 1] = cardsToAlloc * -1;
            }
        }

        // Remove totally unused cards
        for (int i = 0; i < 52; i++){
            if (mPlayingCards[i] == null){
                mUnusedPlayingCards[i] = nDecks;
            }
        }
    }


}
