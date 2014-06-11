/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poketournament;

import mediator.FightMediator;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import playerThread.AiPlayer;
import playerThread.HumanPlayer;
import playerThread.Player;
import view.player.AiView;
import view.player.HumanView;

/**
 *
 * @author Fabio
 */
public class Match extends Observable implements Runnable, Observer {

    private final Pokemon pkmn1;
    private final Pokemon pkmn2;
    private final Pokemon chosenPkmn;
    private final Pokemon ennemyPkmn;
    private HumanView humanView;
    private AiView aiView;
    private Pokemon winner;
    private Boolean autoWin;
    private FightMediator fight;
    private Player player1;
    private Player player2;
    
    public Match(Pokemon pkmn1, Pokemon pkmn2, Boolean autoWin, Pokemon chosenPkmn) {
        this.pkmn1 = pkmn1;
        this.pkmn2 = pkmn2;
        this.chosenPkmn = chosenPkmn;
        if(chosenPkmn == pkmn1)
            ennemyPkmn = pkmn2;
        else
            ennemyPkmn = pkmn1;
        
        this.autoWin = autoWin;
        synchronized(this){
            if (autoWin) {
                setWinner(pkmn1);
            }

            new Thread(this).start();
        }
    }

    public void start() {
        fight = new FightMediator(this, chosenPkmn);
        fight.addObserver(this);
        
        player1 = new HumanPlayer(fight, chosenPkmn, ennemyPkmn);
        player2 = new AiPlayer(fight, ennemyPkmn, chosenPkmn);
  
        humanView = new HumanView((HumanPlayer)player1);
        aiView = new AiView((AiPlayer)player2);
    
    }

    public Pokemon getWinner() {
        return winner;
    }

    public void setWinner(Pokemon pkmn) {
        this.winner = pkmn;
    }

    public Pokemon getPkmn1() {
        return pkmn1;
    }

    public Pokemon getPkmn2() {
        return pkmn2;
    }

    @Override
    public void run() {
        while (getWinner() == null) {
            synchronized (this) {
                try {
                    System.out.println("Attente de resultat pour le match " +
                                pkmn1.getName() + " vs " + pkmn2.getName());
                    wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Match.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        System.out.println("[" + pkmn1.getName() + " vs " + pkmn2.getName()
                            + ": winner = " + getWinner().getName()+"]");

        setChanged();
        notifyObservers();

        if(!autoWin)
         //TODO MESSAGE de fin
         humanView.close();
    }

    @Override
    public void update(Observable o, Object arg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
