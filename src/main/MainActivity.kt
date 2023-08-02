package com.example.wordle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {
    private lateinit var wordList: List<String>    // The list of words
    private lateinit var word: String    // The selected word
    private var gameOver = false    // Is the game over?
    private var guess = "     ";    // The user's guess

    // ANSI COLORS //
    private val ANSI_RESET = "\u001B[0m"   // Reset to default background color
    private val ANSI_GREEN = "\u001B[42m"  // Green background color
    private val ANSI_YELLOW = "\u001B[43m" // Yellow background color
    private val ANSI_BLACK = "\u001B[40m"  // Black background color

    // Check if user's word exists in the file
    private fun legitGuess(): Boolean = guess.lowercase() in wordList

    // build a map<character,count> for the word
    // Key is a letter, value counts occurrences of the letter
    private fun countCharacterOccurrences(str: String): Map<Char, Int> {
        val charCountMap = mutableMapOf<Char, Int>()    // initialize the map
        // how many times the same character appeared in the word
        for (c in str) charCountMap[c] = charCountMap.getOrDefault(c, 0) + 1
        return charCountMap
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Read a file (list of words) used in the game
        // IMPORTANT: You must put your txt file into res/raw
        wordList = BufferedReader(
            InputStreamReader(
                resources.openRawResource(
                    resources.getIdentifier(
                        "wordle",
                        "raw",
                        packageName
                    )
                )
            )
        ).readLines()
        // Pick a word from the file, randomly
        word = wordList.random()
//        word = "slime"  // test only
        // Tell the user what the word is, for debugging
        findViewById<TextView>(R.id.message).text = "The word is $word"
    }

    // Track the cursor position in the Wordle grid
    private var row = 1
    private var col = 1

    // get textView (e.g., textView23) corresponding row and column
    private fun getTextView(row: Int, col: Int): TextView {
        // e.g., idName is textView31
        val idName = if (col > 5) "textView${row}5" else "textView${row}${col}"
        // resources.getIdentifier will return corresponding number (e.g.,2131231192)
        val id = resources.getIdentifier(idName, "id", packageName)
        //println("idName is $idName and id is $id")    //for debugging
        return findViewById<TextView>(id)
    }

    // get letter button (e.g., buttonS, buttonQ)
    private fun getButton(letter: String): Button {
        // e.g., idName is buttonA, buttonB, buttonC, etc
        val idName = "button${letter.uppercase()}"
        // resources.getIdentifier will return corresponding number (e.g., 2131231192)
        val id = resources.getIdentifier(idName, "id", packageName)
        //println("idName is $idName and id is $id")    // for debugging
        return findViewById<Button>(id)
    }

    // themes.xml - OnClickListener for letter buttons
    fun letterHandler(view: View) {
        Log.d("Wordle", "letterHandler"); // testing (Logcat)
        // if game is over, just return
        if (guess == word) gameOver = true

        // when a user press a letter, show the letter to current textView
        getTextView(row, col).text = (view as Button).text.toString()

        //println((view as Button).text.toString())     // for debugging
        // advance cursor to next textView
//        while (!gameOver)
        row = 1
        col += 1

    }

    // themes.xml - OnClickListener for back space
    fun backspaceHandler(view: View) {
        // if game is over, just return
        if (guess == word) gameOver = true

        // if we went past the end, so clamp down
        if (col == 1){
            col = 5 // sends cursor to end without crashing app
        }else
            col -= 1 // moves cursor back


        // Erase the text
        getTextView(row, col).text = ""

    }

    // themes.xml - OnClickListener for enter
    fun enterHandler(view: View) {
        // No change to game state if the word is incomplete
        if (col != 5) gameOver = false

        // grab text from textView and concatenate

        getGuess()
        row += 1
        updateButtonColor("${getButton("$col")}", 1)
//        print("$guess")

        // No change to game state if the word is not in dictionary
//        if (guess in wordList){
//        if (legitGuess(guess)) gameOver = false
        // At this point, reveal the game state


//        colorCode()


        // If we got here, the guessed word is in the dictionary
        // If it matches the word, the game is over
        if (guess == word){ //winning
            gameOver = true
            findViewById<TextView>(R.id.message).text = "you win! :)"
        } else {
            for (i in 1..5)
                if (guess[i] != word[i]){
                    return
                }
            return
        }
        // If we're on the last row, the game is over
        if (row == 5){
            gameOver = true
            findViewById<TextView>(R.id.message).text = "You lose! :("
        } //losing
    }

    // grab text from textView and concatenate
    private fun getGuess() {
//        guess = getTextView(1,1).text.toString() +
//                getTextView(1,2).text.toString() +
//                getTextView(1,3).text.toString() +
//                getTextView(1,4).text.toString() +
//                getTextView(1,5).text.toString()
        for (i in 1..5) {
            guess += getTextView(row, i).text.toString()
        }

        println("Printing guess $guess")
    }

    private fun updateTextColor(row: Int, col: Int, color: Int) {
//        private val ANSI_RESET = "\u001B[0m"   // Reset to default background color
//        private val ANSI_GREEN = "\u001B[42m"  // Green background color
//        private val ANSI_YELLOW = "\u001B[43m" // Yellow background color
//        private val ANSI_BLACK = "\u001B[40m"  // Black background color
        for (i in 1..5)
            if (guess[i] == word[i]) {
                colorCode().add("$ANSI_GREEN${guess[i]}$ANSI_RESET")
            } else if (guess[i] in word) {
                colorCode().add("$ANSI_YELLOW${guess[i]}$ANSI_RESET")
            } else
                colorCode().add("$ANSI_BLACK${guess[i]}$ANSI_RESET")
//
//        if (letter == correct_letter && correct_spot) {
//            color = ANSI_GREEN
//        } elif (letter == correct_letter){
//            color = ANSI_YELLOW
//        } elif (letter != correct_letter){
//            color = ANSI_BLACK
//        }else{
//            color = ANSI_RESET
//        }

    }

    private val colorMap = mutableMapOf<String, Int>()
    private fun updateButtonColor(letter: String, color: Int) {

//         Pick the best color for the button
//         Green beats yellow and gray
//         Yellow beats gray
        for (i in 1..5)
            if (guess[i] == word[i]) {
                getTextView(1,1).text = "$ANSI_GREEN${guess[i]}$ANSI_RESET"
            } else if (guess[i] in word) {
                getTextView(1,1).text = "$ANSI_GREEN${guess[i]}$ANSI_RESET"
            } else
                getTextView(1,1).text = "$ANSI_GREEN${guess[i]}$ANSI_RESET"

    }

    // based on a map<letter, occurrence>, update textView colors and keyboard button colors
    private fun colorCode(): String {
        // Store user's guess as array of strings. Five letters, index 0 to 4.
        val colorGuess = arrayListOf<String>()
        for (i in 0..4) {
            colorGuess.add("$ANSI_BLACK${guess[i]}$ANSI_RESET")
        }
        val charCount = countCharacterOccurrences(word).toMutableMap()
        for (j in 0..4)
            if (guess[j] == word[j]) {
                colorGuess[j] = "$ANSI_GREEN${guess[j]}$ANSI_RESET"
                charCount[guess[j]] = charCount[guess[j]]!! - 1
            }
        for (k in 0..4)
            if (word.contains(guess[k]) && (charCount[guess[k]]!!.toInt() > 0)) {
                colorGuess[k] = "$ANSI_YELLOW${guess[k]}$ANSI_RESET"
                charCount[guess[k]] = charCount[guess[k]]!! - 1
            }
        return colorGuess.joinToString(separator = "")
    }


//        fun gameState(guess: String, word: String): String {
//            /**
//             * Function gameState() takes in the user’s guess and the target word, and returns a color coded version of the
//             * guess based on the rules of Wordle.
//             */
//            val colorCoded = Array(5) { " " }
//            val mapOfWord = countCharacterOccurrences(word).toMutableMap()
//            for (i in 0..4) {
//                if (guess[i] == word[i]) {
//                    colorCoded[i] = "${ ANSI_GREEN }${ word[i] }${ ANSI_RESET }"
//                    mapOfWord[word[i]] = mapOfWord[word[i]]!! - 1
//                }
//            }
//            for (i in 0..4) {
//                if ((guess[i] in mapOfWord) && mapOfWord[guess[i]] != 0) {
//                    colorCoded[i] = "${ ANSI_YELLOW }${ guess[i] }${ ANSI_RESET }"
//                    mapOfWord[guess[i]] = mapOfWord[guess[i]]!! - 1
//                } else if ((guess[i] !in mapOfWord)) {
//                    colorCoded[i] = "${ ANSI_BLACK }${ guess[i] }${ ANSI_RESET }"
//                }
//            }
//        }
//    }
}// ENTIRE CLASS
