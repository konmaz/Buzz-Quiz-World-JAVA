import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class GUI_Main extends GUI_Logic{

    GUI_Main(ArrayList<String> categoriesOfQuestions ) {
        super(categoriesOfQuestions);
    }

    /**
     * Shows a popup and closes the frame
     * Also shows the winners and the leaderboard before terminating the program
     * @param k if k = 0, All Rounds have been completed.
     *          if k = 1, No more questions
     *          if k = -1, Error :(
     */
    public void exitFrame(int k){

        if (k==0)
            JOptionPane.showMessageDialog(frame,"All Rounds have been completed!\nThanks for playing!",
                    "The game has ended", JOptionPane.INFORMATION_MESSAGE);
        if (k==1)
            JOptionPane.showMessageDialog(frame,"Sadly there are no more questions in the directory. You answered them all?\nThanks for playing!",
                    "The game has ended", JOptionPane.INFORMATION_MESSAGE);
        if (k==-1)
            JOptionPane.showMessageDialog(frame,"An error has occurred",
                    "Error", JOptionPane.ERROR_MESSAGE);
        popupShowWinners();
        frame.dispose();
        System.exit(k);
    }


    /**
     * Ask from the user the number of players, and returns the result.
     * @param maxNumberOfPlayers The maximum number of players.
     * @return Returns the number of players.
     */
    public int popupAskNumberOfPlayer(int maxNumberOfPlayers) {
        String [] t = new String[maxNumberOfPlayers];
        t[0] = "1 Player";      //Default number of players if no other players are inserted
        for (int i = 1; i < maxNumberOfPlayers; i++) {
            t[i] = (i+1) + " Players";
        }
        numberOfPlayers = popupInput("Number of Players?", t) + 1;  //Ask and get the number of players
        this.responsesObj = new Responses(numberOfPlayers);
        return numberOfPlayers;
    }

    public String popupGetPlayerName(int i) {

        String temp = JOptionPane.showInputDialog("Enter Name of Player " + i + " :\n","Player "+i);
        if (temp == null || temp.isBlank()){ // If the user closed the window set the default name to be Player + i
            temp = "Player "+i;
        }
        return temp;

    }

    /**
     * This method shows a popup with the results (Correct response, how many points the player gained)
     * @param gainedPointsHash A HashMap containing <b>key:Player object value Integer gained Points.</b>
     * @param correctAnswer A String containing the correct Answer.
     * @param responsesObj A responsesObj to access player responses.
     */
    public void popupShowGainedPoints(HashMap<Player, Integer> gainedPointsHash, String correctAnswer, Responses responsesObj)
    {
        for (JLabel item: txtRes){
            if (item.getText().equals(correctAnswer))//Set the responses to green if is true
                item.setForeground(Color.green);
            else
                item.setForeground(Color.red);//Set the responses to red if is false
        }

        //A method which shows point results according to the answer and the points
        StringBuilder temp = new StringBuilder();
        temp.append("The correct answer was : ").append(correctAnswer).append(".\n\n");
        for (int i = 0; i < gainedPointsHash.size(); i++) {
            Player currentPlayer = responsesObj.getPlayerAtPos(i);
            if (!gainedPointsHash.containsKey(currentPlayer) || currentPlayer == null)  //If an error occurs with a player, terminate the loop
                continue;
            if (responsesObj.getResponseAtPos(i).equals(correctAnswer))     //If a player answers correctly, make his name green
                changePlayerStatusToTrue(currentPlayer);
            else
                changePlayerStatusToFalse(currentPlayer);                   //If a player answers incorrectly, make his name red

            if (gainedPointsHash.get(currentPlayer) == 0){
                temp.append(currentPlayer.getName()).append(" didn't get any points.\n");   //The corresponding player got exactly 0 points
            }
            else {
                if (gainedPointsHash.get(currentPlayer) >= 0){
                    temp.append(currentPlayer.getName()).append(" gained : ").append(gainedPointsHash.get(currentPlayer));  //The corresponding player got positive points
                }
                else {
                    temp.append(currentPlayer.getName()).append(" lost : ").append(gainedPointsHash.get(currentPlayer) * (-1)); //The corresponding player got negative points
                }
                temp.append(" points.\n");
            }
        }temp.append("\n\n");

        JOptionPane.showMessageDialog(this.frame, temp.toString(),
                "Results", JOptionPane.PLAIN_MESSAGE);      //Print the above results into the frame
    }

    /**
     * This method adds the players in the bottom panel.
     * Also this method adds the respond keys.
     * <b>This method adds the playerArr to the private fields of GUI</b>
     * @param playersArr A Array containing Player objects.
     */
    public void drawPlayersInfoToGUI(Player[] playersArr) {
        this.playersArr = playersArr;
        playerToJLabel_HashMap = new HashMap<>();

        //Adds each player's name into the bottom panel as well as their score
        for (Player player : playersArr) {
            playerToJLabel_HashMap.put(player, new JLabel(player.getName() + ":" + player.getPoints()));
            playerToJLabel_HashMap.get(player).setFont(font_Verdana_Bold26);
            playerToJLabel_HashMap.get(player).setForeground(Color.WHITE);
            scorePanel.add(playerToJLabel_HashMap.get(player));
            scorePanel.revalidate();
            scorePanel.add(Box.createHorizontalGlue());
            scorePanel.revalidate();
            //System.out.println("i :" + i + " name : " + playersArr[i].getName());
        }


        for (int i = 0; i < numberOfResponses; i++) {
            StringBuilder temp = new StringBuilder();
            for (int j = 0; j < numberOfPlayers; j++) {
                temp.append(playersArr[j].getKeyboard_responses()[i]).append(" ");
            }
            txtResKeys[i].setText(temp.toString());

        }

        //Adds corresponding response keys on the screen for each player
        for (int i = 0; i < numberOfResponses; i++) {
            for (Player playerObj : playersArr) {
                characterToJLabel_HashMap.put(playerObj.getKeyboard_responses()[i], txtRes[i]);
                characterToPlayer_HashMap.put(playerObj.getKeyboard_responses()[i], playerObj);
            }
        }
    }

    /**
     * This method updates the score JLabel in the bottom panel for every player.
     *
     * @param playersArr A Array containing Players objects.
     */
    public void updatePlayersPoints(Player[] playersArr) {
        for (Player item : playersArr) {
            JLabel label_p = playerToJLabel_HashMap.get(item);
            String temp = label_p.getText().substring(0, label_p.getText().indexOf(':') + 1);
            temp += item.getPoints();
            label_p.setText(temp);
        }

    }

    /**
     * This method adds the winner(s) of the game
     * into the leaderboard and prints them on a popup window
     * It also runs the following popup for the leaderboard
     * (which runs in GUI.java)
     */
    public void popupShowWinners(){

        //Check who won to present him
        Utilities.whoWon(playersArr);

        // Add player higher
        for (Player item : playersArr)
            highScoresObj.addHighScore(item);

        StringBuilder temp = new StringBuilder();
        int numberOfWinners = 0;
        for (Player item : playersArr) {
            if (item.getHasWon()) {

                temp.append(item.getName());
                temp.append(" won with ");
                temp.append(item.getPoints());
                temp.append(" points.");
                numberOfWinners++;
            }
            else{
                temp.append(item.getName());
                temp.append(" got ");
                temp.append(item.getPoints());
                temp.append(" points.");
            }
            temp.append("\n");
            if (numberOfWinners > 1)
                temp.append("It was a draw");
        }
        JOptionPane.showMessageDialog(frame, temp);
        popupLeaderboard(highScoresObj.getHighScoresTable());

    }







}
