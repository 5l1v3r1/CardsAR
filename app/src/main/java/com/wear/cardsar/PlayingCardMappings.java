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

    private boolean ready = false;

    public static final String[] playingCardSuits = {"Clubs", "Diamonds", "Hearts", "Spades"};
    public static final String[] playingCardRanks = {"Ace", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Jack", "King", "Queen"};

    public static String getPlayingCardName(int cardID){
        int suit = cardID / 13;
        int rank = cardID % 13;

        return playingCardRanks[rank] + " of " + playingCardSuits[suit];
    }

    public PlayingCardMappings(Game game, AppRepository repository){
        //mapToPlayingCards(game, repository);
        initialize(game, repository);
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

    private void setReady(){
        synchronized (this) {
            ready = true;
        }
    }

    /*
    private static List<CardMapping> loadMappings(Game game, AppRepository repository){


        if (liveMappingData.getValue() != null){
            return liveMappingData.getValue();
        }else{
            Log.d("PlayingCardMappings", "livedata contained null");
            return new LinkedList<>();
        }
    }
    */

    private void initialize(Game game, AppRepository repository){

        nPlayingCards = 0;
        nUniqueCards = 0;

        LiveData<List<CardMapping>> liveMappingData = repository.getMappings(game.getGameName());

        final PlayingCardMappings receiver = this;

        liveMappingData.observeForever(new Observer<List<CardMapping>>() {

            @Override
            public void onChanged(@Nullable List<CardMapping> cardMappings) {
                if (cardMappings == null) return;
                for (CardMapping mapping : cardMappings){
                    nUniqueCards++;
                    nPlayingCards += mapping.getQuantity();
                }

                int nDecks = (nPlayingCards / 52) + 1;

                for (CardMapping mapping : cardMappings){
                    int cardsToAlloc = mapping.getQuantity();
                    while (cardsToAlloc > 0){
                        mPlayingCards[lastAvailablePCard] = mapping;
                        lastAvailablePCard++;

                        // after mapping to a
                        cardsToAlloc -= nDecks;
                    }

                    // Each unique card is mapped to a playing card
                    // If there are multiple decks, and not enough of unique mapping,
                    // extra cards from other decks must be removed
                    if (cardsToAlloc < 0){
                        mUnusedPlayingCards[lastAvailablePCard - 1] = cardsToAlloc * -1;
                    }
                }

                Log.d("PlayingCardMapping", "changed and loaded list of size " + cardMappings.size());

                receiver.setReady();
            }

        });
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

    /*
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
    }
    */

}
