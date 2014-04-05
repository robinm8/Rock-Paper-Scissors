package workspace;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * Links my game to save and load local xml data from "RPS_Data.xml" in the user
 * working directory.
 * 
 * @author Mark Robinson
 * 
 */
public class XMLStore {

	File xmlFile = new File(System.getProperty("user.dir") + "\\RPS_Data.xml");

	/**
	 * Makes a string representation of a game's statistics in xml form.
	 * 
	 * @param gameName
	 *            - A unique name assigned to each game as an xml attribute
	 * @return - A string representing a game's statistics
	 */
	public String getGameStatistics(int gameName) {
		String statistics = "";

		Element game = getGameElement(gameName);

		if (game != null) {

			statistics += "Best Of: " + game.getAttribute("bestOf") + " \n";
			statistics += "Round Wins: " + game.getAttribute("playerWins")
					+ " \n";
			statistics += "Round Ties: " + game.getAttribute("playerTies")
					+ " \n";
			statistics += "Round Losses: " + game.getAttribute("playerLoses")
					+ " \n";
			statistics += "Started: " + game.getAttribute("started") + " \n";
			statistics += "Ended: " + game.getAttribute("ended") + " \n";
			statistics += "Game Winner: " + game.getAttribute("gameWinner");

		}
		return statistics;
	}

	/**
	 * Makes an array of statistics in String from, from the global xml element
	 * 
	 * @return An array of formatted Strings {Games Started, Games Won, Games
	 *         Lost, Games Pending, Rounds Started, Rounds Won, Rounds Tied,
	 *         Rounds Lost}
	 */
	public String[] getSavedStatistics() {
		String[] statistics = new String[8];
		Element globalSettings = (Element) Link.doc.getElementsByTagName(
				"globalSettings").item(0);
		int roundsStarted = Integer.parseInt(globalSettings
				.getAttribute("totalUserRoundLoses"))
				+ Integer.parseInt(globalSettings
						.getAttribute("totalUserRoundWins"))
				+ Integer.parseInt(globalSettings
						.getAttribute("totalUserRoundTies"));
		int gamesPending = Integer.parseInt(globalSettings
				.getAttribute("totalUserGamesInitiated"))
				- (Integer.parseInt(globalSettings
						.getAttribute("totalUserGameWins")) + Integer
						.parseInt(globalSettings
								.getAttribute("totalUserGameLoses")));

		statistics[0] = "Games Started: "
				+ globalSettings.getAttribute("totalUserGamesInitiated");
		statistics[1] = "Games Won: "
				+ globalSettings.getAttribute("totalUserGameWins");
		statistics[2] = "Games Lost: "
				+ globalSettings.getAttribute("totalUserGameLoses");
		statistics[3] = "Games Pending: " + gamesPending;
		statistics[4] = "Rounds Started: " + roundsStarted;
		statistics[5] = "Rounds Won: "
				+ globalSettings.getAttribute("totalUserRoundWins");
		statistics[6] = "Rounds Tied: "
				+ globalSettings.getAttribute("totalUserRoundTies");
		statistics[7] = "Rounds Lost: "
				+ globalSettings.getAttribute("totalUserRoundLoses");

		return statistics;
	}

	/**
	 * Creates an array of Strings from the list parameter that is clean from
	 * any cells set 'null'.
	 * 
	 * @param list
	 *            - Source array of Strings
	 * @return - An array of Strings clean of all null cells.
	 */
	public String[] cleanList(String[] list) {
		int validItems = 0;

		for (int index = 0; index < list.length; index++) {
			if (list[index] != null) {
				validItems++;
			}
		}

		String[] newList = new String[validItems];
		System.arraycopy(list, 0, newList, 0, validItems);

		return newList;
	}

	/**
	 * Converts an array of game Elements into a formatted String list
	 * 
	 * @param An
	 *            array of game Elements for conversion
	 * @return An array of formatted Strings
	 */
	public String[] convertGamesListForDisplay(Element[] list) {
		String[] gamesWinners = new String[list.length];

		for (int index = 0; index < list.length; index++) {
			if (list[index] != null) {
				Element game = list[index];

				gamesWinners[index] = String.format("Game %s - %s Victory",
						game.getAttribute("name"),
						game.getAttribute("gameWinner"));
			}
		}

		return gamesWinners;
	}

	/**
	 * Creates a list of games pending completion.
	 * 
	 * @return Array of saved games in Element form.
	 */
	public Element[] getSavedGamesList() {
		Element[] list = new Element[Link.doc.getDocumentElement()
				.getElementsByTagName("game").getLength()];
		int savedIndex = 0;
		for (int index = 0; index < Link.doc.getDocumentElement()
				.getElementsByTagName("game").getLength(); index++) {

			if (Link.doc.getDocumentElement().getElementsByTagName("game")
					.item(index).getAttributes().getNamedItem("gameWinner")
					.getTextContent().equals("Pending")) {
				list[savedIndex] = (Element) Link.doc.getDocumentElement()
						.getElementsByTagName("game").item(index);
				savedIndex++;
			}
		}
		return list;
	}

	/**
	 * Attempts to find a game xml element with a 'name' attribute equal to the
	 * 'gameName' parameter.
	 * 
	 * @param gameName
	 *            - Unique number to find an xml Element
	 * @return - If a matching xml Element is found, it is returned as an xml
	 *         element. Otherwise, null is returned.
	 */
	public Element getGameElement(int gameName) {
		for (int index = 0; index < Link.doc.getDocumentElement()
				.getElementsByTagName("game").getLength(); index++) {
			Element game = (Element) Link.doc.getDocumentElement()
					.getElementsByTagName("game").item(index);
			if (game.getAttribute("name").equals(String.valueOf(gameName))) {
				return game;
			}
		}
		return null;
	}

	/**
	 * Creates a list of all created games.
	 * 
	 * @return Array of created games in Element form.
	 */
	public Element[] getGamesList() {
		Element[] list = new Element[Link.doc.getDocumentElement()
				.getElementsByTagName("game").getLength()];
		for (int index = 0; index < Link.doc.getDocumentElement()
				.getElementsByTagName("game").getLength(); index++) {
			list[index] = (Element) Link.doc.getDocumentElement()
					.getElementsByTagName("game").item(index);
		}
		return list;
	}

	/**
	 * Sets the game winner attribute within the active game element based on
	 * the game winner.
	 * 
	 */
	public void determineAndSetGameWinner() {
		Element globalSettings = (Element) Link.doc.getElementsByTagName(
				"globalSettings").item(0);
		if (determineGameWinner() == 1) {
			globalSettings.setAttribute("totalUserGameWins", String
					.valueOf(Integer.parseInt(globalSettings
							.getAttribute("totalUserGameWins")) + 1));
			setAttributeOfActiveGameElement("gameWinner", "Player");
		} else {
			globalSettings.setAttribute("totalUserGameLoses", String
					.valueOf(Integer.parseInt(globalSettings
							.getAttribute("totalUserGameLoses")) + 1));
			setAttributeOfActiveGameElement("gameWinner", "CPU");
		}
	}

	/**
	 * Determines the game winner based on player round wins vs player round
	 * losses
	 * 
	 * @return - The winner of the game in int form. (0:Tie Game;1:Player;
	 *         2:CPU)
	 */
	public int determineGameWinner() {
		int playerRoundWins = Integer
				.parseInt(getAttributeOfActiveGameElement("playerWins"));
		int playerRoundLoses = Integer
				.parseInt(getAttributeOfActiveGameElement("playerLoses"));
		if (playerRoundWins > playerRoundLoses) {
			return 1;
		} else if (playerRoundWins < playerRoundLoses) {
			return 2;
		}
		return 0;
	}

	/**
	 * Gets an attribute of the active game element in use.
	 * 
	 * @param name
	 *            - The name of the attribute to retrieve.
	 * @return - The value of the attribute in string form. If nonexistent,
	 *         returns null.
	 */
	public String getAttributeOfActiveGameElement(String name) {
		if (getActiveGameElement() != null) {
			return getActiveGameElement().getAttribute(name);
		}
		return null;
	}

	/**
	 * Sets an attribute of the active game element in use.
	 * 
	 * @param name
	 *            - The name of the attribute to create or alter.
	 * @param to
	 *            - Value to set in string form.
	 */
	public void setAttributeOfActiveGameElement(String name, String to) {
		if (getActiveGameElement() != null
				&& getActiveGameElement().hasAttribute(name)) {
			getActiveGameElement().setAttribute(name, to);
		}
	}

	/**
	 * Uses Link.activeGame to attempt identifying and will return a game
	 * element tagged to that number if found.
	 * 
	 * @return - If found, returns an Element. If the element cannot be found,
	 *         it returns null.
	 */
	public Element getActiveGameElement() {
		for (int ni = 0; ni < Link.doc.getDocumentElement()
				.getElementsByTagName("game").getLength(); ni++) {
			if (Link.activeGame.equals(Link.doc.getElementsByTagName("game")
					.item(ni).getAttributes().getNamedItem("name")
					.getTextContent())
					|| Link.activeGame == Link.doc.getElementsByTagName("game")
							.item(ni).getAttributes().getNamedItem("name")
							.getTextContent()) {
				return (Element) Link.doc.getElementsByTagName("game").item(ni);
			}
		}
		return null;
	}

	/**
	 * Counts the number of elements in the saved XML document that are named
	 * "game" and adds one to that number.
	 * 
	 * @return - A string representing the next available number to create an
	 *         identifying tag for a game.
	 */
	public String getAvailableNumberToNameGame() {
		String name = "0";
		boolean repeat = true;
		do {
			name = String.valueOf(Integer.parseInt(name) + 1);
			boolean found = false;
			for (int ni = 0; ni < Link.doc.getDocumentElement()
					.getElementsByTagName("game").getLength(); ni++) {
				if (name.equals(Link.doc.getElementsByTagName("game").item(ni)
						.getAttributes().getNamedItem("name").getTextContent())
						|| name == Link.doc.getElementsByTagName("game")
								.item(ni).getAttributes().getNamedItem("name")
								.getTextContent()) {
					found = true;
					break;
				}
			}
			if (found == false) {
				repeat = false;
			}
		} while (repeat == true);
		return name;
	}

	/**
	 * Creates a String representing the active game's current score as a ratio;
	 * Player:CPU round wins.
	 * 
	 * @return Ex: "Score: 2 Rounds Won : 1 Round Lost"
	 */
	public String getScoreDisplay() {
		String display = "";

		Element activeGame = getActiveGameElement();

		if (activeGame != null) {

			display = "Score - ";

			int roundsWon = Integer.parseInt(activeGame
					.getAttribute("playerWins"));
			int roundsTied = Integer.parseInt(activeGame
					.getAttribute("playerTies"));
			int roundsLost = Integer.parseInt(activeGame
					.getAttribute("playerLoses"));

			display += roundsWon
					+ (roundsWon > 1 || roundsWon < 1 ? " Rounds " : " Round")
					+ " Won : " + roundsTied
					+ (roundsTied > 1 || roundsTied < 1 ? " Rounds" : " Round")
					+ " Tied : " + +roundsLost
					+ (roundsLost > 1 || roundsLost < 1 ? " Rounds" : " Round")
					+ " Lost";

		}
		return display == "" ? "Score - 0 Rounds Won : 0 Rounds Lost" : display;
	}

	/**
	 * Creates a new xml element and places it in the saved xml document. Also
	 * sets the activeGame variable to the new element's ID number.
	 */
	public void makeNewGame() {
		Link.activeGame = getAvailableNumberToNameGame();

		Element globalSettings = (Element) Link.doc.getElementsByTagName(
				"globalSettings").item(0);

		globalSettings.setAttribute("totalUserGamesInitiated", String
				.valueOf(Integer.parseInt(globalSettings
						.getAttribute("totalUserGamesInitiated")) + 1));

		Element game = Link.doc.createElement("game");
		game.setAttribute("name", getAvailableNumberToNameGame());
		game.setAttribute("started", Link.time.toString());
		game.setAttribute("ended", "Pending");
		game.setAttribute("bestOf",
				Link.doc.getElementsByTagName("globalSettings").item(0)
						.getAttributes().getNamedItem("bestOf").getNodeValue());
		game.setAttribute("currentRound", "1");
		game.setAttribute("gameWinner", "Pending");
		game.setAttribute("playerWins", "0");
		game.setAttribute("playerLoses", "0");
		game.setAttribute("playerTies", "0");
		Link.doc.getDocumentElement().appendChild(game);
	}

	/**
	 * When you ask to "create", the method creates a new XML document if it
	 * does not already exist. Then it saves the document in the user working
	 * directory. When you ask to "save", the method saves Link.doc to the XML
	 * document in the application's working directory.
	 * 
	 * @param w
	 *            ("save", "create")
	 */
	public void Do(String w) {

		if (w == "load") {
			try {
				if (xmlFile.exists()) {
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					Document doc = dBuilder.parse(xmlFile);
					doc.getDocumentElement().normalize();
					Link.doc = doc;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (w == "create") {
			try {
				if (xmlFile.exists()) {
					this.Do("load");
				} else {

					DocumentBuilderFactory docFactory = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder docBuilder = docFactory
							.newDocumentBuilder();

					// root elements
					Document doc = docBuilder.newDocument();

					Element rootElement = doc.createElement("Data");
					doc.appendChild(rootElement);

					Element globalSettings = doc
							.createElement("globalSettings");
					globalSettings.setAttribute("bestOf", "3");
					globalSettings.setAttribute("totalUserGamesInitiated", "0");
					globalSettings.setAttribute("totalUserGameWins", "0");
					globalSettings.setAttribute("totalUserGameLoses", "0");
					globalSettings.setAttribute("totalUserRoundWins", "0");
					globalSettings.setAttribute("totalUserRoundLoses", "0");
					globalSettings.setAttribute("totalUserRoundTies", "0");
					doc.getDocumentElement().appendChild(globalSettings);

					Link.doc = doc;
					OutputFormat format = new OutputFormat(Link.doc);
					format.setIndenting(true);

					XMLSerializer serializer;
					serializer = new XMLSerializer(new FileOutputStream(
							new File(System.getProperty("user.dir")
									+ "\\RPS_Data.xml")), format);
					serializer.serialize(Link.doc);

					docFactory = null;
					docBuilder = null;
					rootElement = null;
					doc = null;
					format = null;
					serializer = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (w == "save" && Link.doc != null) {

			OutputFormat format = new OutputFormat(Link.doc);
			format.setIndenting(true);

			XMLSerializer serializer;

			try {
				serializer = new XMLSerializer(new FileOutputStream(new File(
						System.getProperty("user.dir") + "\\RPS_Data.xml")),
						format);
				serializer.serialize(Link.doc);
				serializer = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			format = null;

		}
		try {
			finalize();
		} catch (Throwable e) {
		}
	}
}