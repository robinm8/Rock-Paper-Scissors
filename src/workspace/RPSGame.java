package workspace;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.w3c.dom.Element;

/**
 * Creates a custom JPanel with a painted background image.
 * 
 * @author Mark Robinson
 */
class BackgroundPanel extends JPanel {
	private static final long serialVersionUID = 7360623640101506948L;
	private Image image;

	/**
	 * Prepares for the image to be drawn to the JPanel.
	 * 
	 * @param layout
	 *            - Layout of the custom JPanel.
	 * @param image
	 *            - Image to the background of the custom JPanel
	 */
	public BackgroundPanel(LayoutManager layout, Image image) {
		this.image = image;
		repaint();
		setLayout(layout);
	}

	/**
	 * Overrides the paintComponent to add my background image.
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image == null) {
			return;
		}
		g.drawImage(image, 0, 0, getSize().width, getSize().height, null);
	}
}

/**
 * My Rock Paper Scissors game with necessary methods to render and play the
 * game.
 * 
 * @author Mark Robinson
 */
public class RPSGame extends JFrame {
	private static final long serialVersionUID = -5795852986231833192L;
	private Toolkit toolkit = Toolkit.getDefaultToolkit();
	private Dimension screenSize = toolkit.getScreenSize();
	private String userSelected = "";
	private String cpuSelected = "";
	private JPanel content = null;

	private JPanel gameTopStatus = new JPanel(new BorderLayout());

	private JPanel statsMenu = new JPanel(new BorderLayout());

	private JPanel startMenuContainer = new JPanel(new GridLayout(3, 0));
	private JPanel startMenu = new JPanel(new BorderLayout());

	private JPanel gameMenuContainer = new JPanel(new GridLayout(0, 3));
	private JPanel gameMenu = new JPanel(new BorderLayout());

	private JPanel continueGameMenuContainer = new JPanel(new GridLayout(0, 1));
	private JPanel continueGameMenu = new JPanel(new BorderLayout());

	private JPanel winner = new JPanel(new BorderLayout());
	private JPanel winnerTextContainer = new JPanel(new GridLayout(0, 2));
	private JPanel winnerContainer = new JPanel(new GridLayout(0, 3));

	private JList allGames = new JList();
	private JScrollPane allGamesScroller = new JScrollPane(allGames);

	private JList savedGames = new JList();
	private JScrollPane savedGamesScroller = new JScrollPane(savedGames);

	private JTextArea gameStatistics = new JTextArea(10, 10);
	private JScrollPane gameStatisticsScroller = new JScrollPane(gameStatistics);

	final JTextArea savedGameStatistics = new JTextArea();
	JScrollPane statisticsScroller = new JScrollPane(savedGameStatistics);

	private JLabel roundStatus = new JLabel("Round 1 of NA");
	private JLabel gameScore = new JLabel("Score: NA Player Wins: NA CPU Wins");
	private JLabel footer = new JLabel("Mark Robinson");

	private JLabel cpuText = new JLabel("CPU");
	private JLabel userText = new JLabel("You");
	private JLabel userSelectedImg = new JLabel();
	private JLabel cpuSelectedImg = new JLabel();
	private JLabel winnerText = new JLabel("Calculating round winner.");
	private JLabel gameText = new JLabel();

	private JLabel totalGames = new JLabel("Games Started: ");
	private JLabel gamesWon = new JLabel("Games Won: ");
	private JLabel gamesLost = new JLabel("Games Lost: ");
	private JLabel gamesPending = new JLabel("Games Pending: ");
	private JLabel totalRounds = new JLabel("Rounds Started: ");
	private JLabel roundsWon = new JLabel("Rounds Won: ");
	private JLabel roundsTied = new JLabel("Rounds Tied: ");
	private JLabel roundsLost = new JLabel("Rounds Lost: ");

	/**
	 * Syncs stat data to display objects.
	 */
	public void syncStatsForDisplay() {
		String[] data = Link.localDataStore.getSavedStatistics();
		totalGames.setText(data[0]);
		gamesWon.setText(data[1]);
		gamesLost.setText(data[2]);
		gamesPending.setText(data[3]);
		totalRounds.setText(data[4]);
		roundsWon.setText(data[5]);
		roundsTied.setText(data[6]);
		roundsLost.setText(data[7]);
	}

	/**
	 * Determines if either the cpu or the user won the round.
	 * 
	 * @return String representing who won the round.
	 */
	public String determineWinner() {
		if (cpuSelected == userSelected) {
			return "Nobody won the round. Throw again!";
		} else if (cpuSelected == "rock" && userSelected == "scissor") {
			return "The CPU won the round. Rock beats scissor.";
		} else if (cpuSelected == "paper" && userSelected == "rock") {
			return "The CPU won the round. Paper beats rock.";
		} else if (cpuSelected == "scissor" && userSelected == "paper") {
			return "The CPU won the round. Scissor beats paper.";
		} else if (userSelected == "rock" && cpuSelected == "scissor") {
			return "You won the round! Rock beats scissor.";
		} else if (userSelected == "paper" && cpuSelected == "rock") {
			return "You won the round! Paper beats rock.";
		} else if (userSelected == "scissor" && cpuSelected == "paper") {
			return "You won the round! Scissor beats paper.";
		}
		return "Nobody won because of an error.";
	}

	/**
	 * Determines if the user won the round.
	 * 
	 * @return int representing if the user or CPU won the round or a tie
	 *         occured. (-1: error; 0: user won; 1: CPU won; 2: Tie)
	 */
	public int roundWinner() {
		if (cpuSelected == userSelected) {
			return 2;
		} else if (cpuSelected == "rock" && userSelected == "scissor") {
			return 1;
		} else if (cpuSelected == "paper" && userSelected == "rock") {
			return 1;
		} else if (cpuSelected == "scissor" && userSelected == "paper") {
			return 1;
		} else if (userSelected == "rock" && cpuSelected == "scissor") {
			return 0;
		} else if (userSelected == "paper" && cpuSelected == "rock") {
			return 0;
		} else if (userSelected == "scissor" && cpuSelected == "paper") {
			return 0;
		}
		return -1;
	}

	/**
	 * Simulates the cpu choosing a destroyer at random.
	 * 
	 * @return An integer between 1 and 3 which represents the destroyer was
	 *         selected by the cpu
	 */
	public int cpuChoose() {
		return (int) (Math.random() * (3 - 1 + 1)) + 1;
	}

	/**
	 * Switches the game view from the menu to the results of the round.
	 * 
	 * @param input
	 *            - The input String representing which image was selected by
	 *            the user.
	 */
	public void chosen(String input) {

		Element globalSettings = (Element) Link.doc.getElementsByTagName(
				"globalSettings").item(0);

		userSelected = input;
		userText.setText("You - " + input);

		try {
			userSelectedImg.setIcon(new ImageIcon(ImageIO.read(new File("imgs/"
					+ input + ".png"))));
		} catch (IOException e) {
		}

		int cpu = cpuChoose();
		cpuSelected = cpu == 1 ? "rock" : cpu == 2 ? "paper" : "scissor";
		cpuText.setText("CPU - " + cpuSelected);

		try {
			cpuSelectedImg.setIcon(new ImageIcon(ImageIO.read(new File("imgs/"
					+ cpuSelected + ".png"))));
		} catch (IOException e) {
		}

		if (roundWinner() == 0) {
			globalSettings.setAttribute("totalUserRoundWins", String
					.valueOf(Integer.parseInt(globalSettings
							.getAttribute("totalUserRoundWins")) + 1));
			Link.localDataStore
					.setAttributeOfActiveGameElement(
							"playerWins",
							String.valueOf(Integer.parseInt(Link.localDataStore
									.getAttributeOfActiveGameElement("playerWins")) + 1));
		} else if (roundWinner() == 1) {
			globalSettings.setAttribute("totalUserRoundLoses", String
					.valueOf(Integer.parseInt(globalSettings
							.getAttribute("totalUserRoundLoses")) + 1));
			Link.localDataStore
					.setAttributeOfActiveGameElement(
							"playerLoses",
							String.valueOf(Integer.parseInt(Link.localDataStore
									.getAttributeOfActiveGameElement("playerLoses")) + 1));
		} else if (roundWinner() == 2) {
			globalSettings.setAttribute("totalUserRoundTies", String
					.valueOf(Integer.parseInt(globalSettings
							.getAttribute("totalUserRoundTies")) + 1));
			Link.localDataStore
					.setAttributeOfActiveGameElement(
							"playerTies",
							String.valueOf(Integer.parseInt(Link.localDataStore
									.getAttributeOfActiveGameElement("playerTies")) + 1));
			Link.localDataStore.getActiveGameElement().setAttribute(
					"currentRound",
					String.valueOf(Integer.parseInt(Link.localDataStore
							.getActiveGameElement()
							.getAttribute("currentRound")) - 1));
		}

		if (Integer.parseInt(Link.localDataStore.getActiveGameElement()
				.getAttribute("currentRound")) == Integer
				.parseInt(Link.localDataStore.getActiveGameElement()
						.getAttribute("bestOf"))) {

			if (Link.localDataStore.determineGameWinner() == 0) {
				gameText.setText("Game tie breaker round is required.");

				Link.localDataStore.getActiveGameElement()
						.setAttribute(
								"bestOf",
								String.valueOf(Integer
										.parseInt(Link.localDataStore
												.getActiveGameElement()
												.getAttribute("bestOf")) + 1));
			} else if (Link.localDataStore.determineGameWinner() == 1) {
				gameText.setText("You won this game with "
						+ Link.localDataStore
								.getAttributeOfActiveGameElement("playerWins")
						+ " wins : "
						+ Link.localDataStore
								.getAttributeOfActiveGameElement("playerLoses")
						+ " losses.");
			} else if (Link.localDataStore.determineGameWinner() == 2) {
				gameText.setText("The CPU won this game with "
						+ Link.localDataStore
								.getAttributeOfActiveGameElement("playerLoses")
						+ " wins : "
						+ Link.localDataStore
								.getAttributeOfActiveGameElement("playerWins")
						+ " losses.");
			}

		}

		winnerText.setText(determineWinner());
		gameMenu.setVisible(false);
		winner.setVisible(true);
		content.remove(gameMenu);
		content.add(winner, BorderLayout.CENTER);

	}

	/**
	 * Builds a panel to show all user games and their statistics.
	 */
	public void buildStats() {
		JPanel center = new JPanel(new BorderLayout());
		JPanel gamesLabelCorrection = new JPanel(new GridLayout(0, 3));
		JPanel topCenter = new JPanel(new GridLayout(0, 1));
		JPanel gameCenter = new JPanel(new GridLayout(1, 0));
		JPanel roundCenter = new JPanel(new GridLayout(1, 0));
		JLabel title = new JLabel("Game Statistics");
		JLabel gamesTitle = new JLabel("List of Games");
		JButton backToMenu = new JButton("Back to Menu");

		gamesTitle.setHorizontalAlignment(SwingConstants.CENTER);
		gamesTitle.setFont(new Font("Verdana", Font.BOLD, 20));

		totalGames.setHorizontalAlignment(SwingConstants.CENTER);
		totalGames.setFont(new Font("Verdana", Font.BOLD, 18));
		gamesWon.setHorizontalAlignment(SwingConstants.CENTER);
		gamesWon.setFont(new Font("Verdana", Font.BOLD, 15));
		gamesLost.setHorizontalAlignment(SwingConstants.CENTER);
		gamesLost.setFont(new Font("Verdana", Font.BOLD, 15));
		gamesPending.setHorizontalAlignment(SwingConstants.CENTER);
		gamesPending.setFont(new Font("Verdana", Font.BOLD, 15));
		totalRounds.setHorizontalAlignment(SwingConstants.CENTER);
		totalRounds.setFont(new Font("Verdana", Font.BOLD, 18));
		roundsLost.setHorizontalAlignment(SwingConstants.CENTER);
		roundsLost.setFont(new Font("Verdana", Font.BOLD, 15));
		roundsWon.setHorizontalAlignment(SwingConstants.CENTER);
		roundsWon.setFont(new Font("Verdana", Font.BOLD, 15));
		roundsTied.setHorizontalAlignment(SwingConstants.CENTER);
		roundsTied.setFont(new Font("Verdana", Font.BOLD, 15));

		allGames.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		allGames.setLayoutOrientation(JList.VERTICAL);
		allGames.setVisibleRowCount(0);
		allGames.setFont(new Font("Verdana", Font.BOLD, 20));
		allGames.setOpaque(false);
		allGames.setBackground(new Color(0, 0, 0, 20));

		allGames.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				String statistics = Link.localDataStore
						.getGameStatistics(allGames.getSelectedIndex() + 1);
				gameStatistics.setText(statistics);
			}
		});

		allGamesScroller.setOpaque(false);
		allGamesScroller.getViewport().setOpaque(false);
		allGamesScroller.setBorder(BorderFactory.createEmptyBorder());
		allGamesScroller
				.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

		gameStatisticsScroller.setOpaque(false);
		gameStatisticsScroller.getViewport().setOpaque(false);
		gameStatisticsScroller.setBorder(BorderFactory.createEmptyBorder());

		gameStatistics.setOpaque(false);
		gameStatistics.setEditable(false);
		gameStatistics.setFocusable(false);
		gameStatistics.setFont(new Font("Verdana", Font.BOLD, 20));

		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(new Font("Verdana", Font.BOLD, 50));
		title.setOpaque(false);

		backToMenu.setFont(new Font("Verdana", Font.BOLD, 30));
		backToMenu.setOpaque(false);
		backToMenu.setBackground(new Color(0, 0, 0, 0));
		backToMenu.setBorderPainted(false);
		backToMenu.setFocusPainted(false);
		backToMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				statsMenu.setVisible(false);
				startMenu.setVisible(true);
				content.remove(statsMenu);
				content.add(startMenu, BorderLayout.CENTER);
				content.revalidate();
			}
		});

		gamesLabelCorrection.setOpaque(false);
		roundCenter.setOpaque(false);
		gameCenter.setOpaque(false);
		topCenter.setOpaque(false);
		center.setOpaque(false);

		roundCenter.add(roundsWon);
		roundCenter.add(roundsTied);
		roundCenter.add(roundsLost);

		gameCenter.add(gamesWon);
		gameCenter.add(gamesLost);
		gameCenter.add(gamesPending);

		gamesLabelCorrection.add(gamesTitle);

		topCenter.add(totalGames);
		topCenter.add(gameCenter);
		topCenter.add(totalRounds);
		topCenter.add(roundCenter);
		topCenter.add(new JSeparator());
		topCenter.add(gamesLabelCorrection);

		center.add(allGamesScroller, BorderLayout.WEST);
		center.add(topCenter, BorderLayout.NORTH);
		center.add(gameStatistics, BorderLayout.EAST);

		statsMenu.add(title, BorderLayout.NORTH);
		statsMenu.add(center, BorderLayout.CENTER);
		statsMenu.add(backToMenu, BorderLayout.SOUTH);
		statsMenu.setOpaque(false);
		statsMenu.setVisible(true);
	}

	/**
	 * Builds a panel to ask the user to continue saved games when requested.
	 */
	public void buildContinueSavedGame() {

		JPanel bottomButtons = new JPanel(new BorderLayout());

		JLabel title = new JLabel("Saved Games");
		JButton backToMenu = new JButton("Back to Menu");
		JButton deleteGame = new JButton("Delete Game");
		JButton continueGame = new JButton("Continue Game");

		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(new Font("Verdana", Font.BOLD, 50));
		title.setOpaque(false);

		savedGames
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		savedGames.setLayoutOrientation(JList.VERTICAL);
		savedGames.setVisibleRowCount(0);
		savedGames.setFont(new Font("Verdana", Font.BOLD, 35));
		savedGames.setOpaque(false);
		savedGames.setBackground(new Color(0, 0, 0, 20));

		savedGames.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = -1087488136974689536L;

			public int getHorizontalAlignment() {
				return CENTER;
			}
		});

		savedGames.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (arg0.getValueIsAdjusting()) {
					String[] list = Link.localDataStore
							.convertGamesListForDisplay(Link.localDataStore
									.getGamesList());
					int foundInGameList = 0;
					for (int index = 0; index < list.length; index++) {
						if (list[index].equals(savedGames.getModel()
								.getElementAt(savedGames.getSelectedIndex()))) {
							foundInGameList = index;
						}
					}
					String statistics = Link.localDataStore
							.getGameStatistics(foundInGameList + 1);
					savedGameStatistics.setText(statistics);
				}
			}
		});

		savedGamesScroller.setOpaque(false);
		savedGamesScroller.getViewport().setOpaque(false);
		savedGamesScroller.setBorder(BorderFactory.createEmptyBorder());
		savedGamesScroller
				.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

		savedGameStatistics.setOpaque(false);
		savedGameStatistics.setEditable(false);
		savedGameStatistics.setFocusable(false);
		savedGameStatistics.setFont(new Font("Verdana", Font.BOLD, 20));

		statisticsScroller.setOpaque(false);
		statisticsScroller.getViewport().setOpaque(false);
		statisticsScroller.setBorder(BorderFactory.createEmptyBorder());
		statisticsScroller
				.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

		continueGame.setFont(new Font("Verdana", Font.BOLD, 40));
		continueGame.setOpaque(false);
		continueGame.setBackground(new Color(0, 0, 0, 0));
		continueGame.setBorderPainted(false);
		continueGame.setFocusPainted(false);

		continueGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String[] list = Link.localDataStore
						.convertGamesListForDisplay(Link.localDataStore
								.getGamesList());
				int foundInGameList = 0;
				if (savedGames.getModel().getSize() > 0) {
					for (int index = 0; index < list.length; index++) {
						if (list[index].equals(savedGames.getModel()
								.getElementAt(savedGames.getSelectedIndex()))) {
							foundInGameList = index;
						}
					}

					Element newActiveGameElement = (Element) Link.doc
							.getElementsByTagName("game").item(foundInGameList);

					roundStatus.setText("Round "
							+ newActiveGameElement.getAttribute("currentRound")
							+ " of "
							+ newActiveGameElement.getAttribute("bestOf"));

					roundStatus.setVisible(true);

					Link.activeGame = newActiveGameElement.getAttribute("name");

					gameScore.setText(Link.localDataStore.getScoreDisplay());
					gameScore.setVisible(true);

					continueGameMenu.setVisible(false);
					gameMenu.setVisible(true);
					content.remove(continueGameMenu);
					content.add(gameMenu, BorderLayout.CENTER);
					content.revalidate();
				}
			}
		});

		deleteGame.setFont(new Font("Verdana", Font.BOLD, 40));
		deleteGame.setOpaque(false);
		deleteGame.setBackground(new Color(0, 0, 0, 0));
		deleteGame.setBorderPainted(false);
		deleteGame.setFocusPainted(false);

		deleteGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int result = JOptionPane
						.showConfirmDialog(null,
								"Are you sure that you want to delete this saved game?");
				String[] list = Link.localDataStore
						.cleanList(Link.localDataStore
								.convertGamesListForDisplay(Link.localDataStore
										.getGamesList()));

				if (result == 0 && savedGames.getSelectedIndex() >= 0) {

					int foundInGameList = 0;
					for (int index = 0; index < list.length; index++) {
						if (list[index].equals(savedGames.getModel()
								.getElementAt(savedGames.getSelectedIndex()))) {
							foundInGameList = index;
						}
					}

					System.out.println(Link.doc.getElementsByTagName("game")
							.item(foundInGameList).getAttributes()
							.getNamedItem("name"));

					int gameNum = Integer.parseInt(Link.doc
							.getElementsByTagName("game").item(foundInGameList)
							.getAttributes().getNamedItem("name")
							.getTextContent());

					Link.doc.getDocumentElement().removeChild(
							Link.doc.getElementsByTagName("game").item(
									foundInGameList));

					for (int index = 0; index < Link.localDataStore
							.getGamesList().length; index++) {
						if (Integer.parseInt(Link.localDataStore.getGamesList()[index]
								.getAttribute("name")) > gameNum) {
							Link.localDataStore.getGamesList()[index].setAttribute(
									"name", String.valueOf(Integer
											.parseInt(Link.localDataStore
													.getGamesList()[index]
													.getAttribute("name")) - 1));
						}
					}

					savedGames.setListData(Link.localDataStore.cleanList(Link.localDataStore
							.convertGamesListForDisplay(Link.localDataStore
									.getSavedGamesList())));

					savedGameStatistics.setText("");

				}
			}
		});

		backToMenu.setFont(new Font("Verdana", Font.BOLD, 30));
		backToMenu.setOpaque(false);
		backToMenu.setBackground(new Color(0, 0, 0, 0));
		backToMenu.setBorderPainted(false);
		backToMenu.setFocusPainted(false);
		backToMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				continueGameMenu.setVisible(false);
				startMenu.setVisible(true);

				content.remove(continueGameMenu);
				content.add(startMenu, BorderLayout.CENTER);
				content.revalidate();
			}
		});

		bottomButtons.add(continueGame, BorderLayout.EAST);
		bottomButtons.add(backToMenu, BorderLayout.SOUTH);
		bottomButtons.add(deleteGame, BorderLayout.WEST);
		bottomButtons.setOpaque(false);

		continueGameMenuContainer.add(savedGamesScroller);
		continueGameMenuContainer.add(statisticsScroller);

		continueGameMenu.add(title, BorderLayout.NORTH);

		continueGameMenu.add(bottomButtons, BorderLayout.SOUTH);
		continueGameMenu.setOpaque(false);
		continueGameMenuContainer.setOpaque(false);
		continueGameMenu.setVisible(true);
		continueGameMenu.add(continueGameMenuContainer, BorderLayout.CENTER);

	}

	/**
	 * Builds the start user selection menu
	 */
	public void buildStartMenu() {

		JLabel title = new JLabel("Rock, Paper, Scissors");
		JButton startGame = new JButton("Start Game");
		JButton continueSavedGame = new JButton("Saved Games");
		JButton viewStats = new JButton("View Stats");

		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(new Font("Verdana", Font.BOLD, 50));
		title.setOpaque(false);

		continueSavedGame.setFont(new Font("Verdana", Font.BOLD, 30));
		continueSavedGame.setOpaque(false);
		continueSavedGame.setBackground(new Color(0, 0, 0, 0));
		continueSavedGame.setBorderPainted(false);
		continueSavedGame.setFocusPainted(false);

		startGame.setFont(new Font("Verdana", Font.BOLD, 30));
		startGame.setOpaque(false);
		startGame.setBackground(new Color(0, 0, 0, 0));
		startGame.setBorderPainted(false);
		startGame.setFocusPainted(false);

		viewStats.setFont(new Font("Verdana", Font.BOLD, 30));
		viewStats.setOpaque(false);
		viewStats.setBackground(new Color(0, 0, 0, 0));
		viewStats.setBorderPainted(false);
		viewStats.setFocusPainted(false);

		viewStats.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				allGames.setListData(Link.localDataStore
						.convertGamesListForDisplay(Link.localDataStore
								.getGamesList()));
				syncStatsForDisplay();
				startMenu.setVisible(false);
				statsMenu.setVisible(true);
				content.remove(startMenu);
				content.add(statsMenu, BorderLayout.CENTER);
				content.revalidate();
			}
		});

		startGame.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				new SwingWorker() {
					@Override
					protected Object doInBackground() throws Exception {

						String roundsToPlay = JOptionPane
								.showInputDialog(
										null,
										"How many rounds should be played before determining a winner?",
										3);

						if (!roundsToPlay.isEmpty()) {

							Element gameSettings = (Element) Link.doc
									.getElementsByTagName("globalSettings")
									.item(0);

							gameSettings.setAttribute("bestOf", roundsToPlay);

						}

						roundStatus.setText("Round 1"
								+ " of "
								+ Link.doc
										.getElementsByTagName("globalSettings")
										.item(0).getAttributes()
										.getNamedItem("bestOf").getNodeValue());

						roundStatus.setVisible(true);
						Link.localDataStore.makeNewGame();

						gameScore.setText(Link.localDataStore.getScoreDisplay());
						gameScore.setVisible(true);

						startMenu.setVisible(false);
						gameMenu.setVisible(true);
						content.remove(startMenu);
						content.add(gameMenu, BorderLayout.CENTER);
						content.revalidate();
						content.repaint();
						return e;
					};
				}.execute();
			}
		});

		continueSavedGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {

				startMenu.setVisible(false);
				continueGameMenu.setVisible(true);

				savedGames.setListData(Link.localDataStore
						.cleanList(Link.localDataStore
								.convertGamesListForDisplay(Link.localDataStore
										.getSavedGamesList())));

				savedGameStatistics.setText("");

				content.remove(startMenu);
				content.add(continueGameMenu, BorderLayout.CENTER);
				content.revalidate();
			}
		});

		startMenuContainer.add(startGame);
		startMenuContainer.add(continueSavedGame);
		startMenuContainer.add(viewStats);

		startMenu.add(title, BorderLayout.NORTH);
		startMenu.setOpaque(false);
		startMenuContainer.setOpaque(false);
		startMenu.setVisible(true);
		startMenu.add(startMenuContainer, BorderLayout.CENTER);
		content.add(startMenu, BorderLayout.CENTER);
	}

	/**
	 * Builds the game user selection menu
	 */
	public void buildGameMenu() {
		JLabel instructions = new JLabel("Choose Your Destroyer!");
		JButton rock = new JButton();
		JButton paper = new JButton();
		JButton scissor = new JButton();
		rock.setOpaque(false);
		rock.setContentAreaFilled(false);
		rock.setBorderPainted(false);
		rock.setFocusPainted(false);
		paper.setOpaque(false);
		paper.setContentAreaFilled(false);
		paper.setBorderPainted(false);
		paper.setFocusPainted(false);
		scissor.setOpaque(false);
		scissor.setContentAreaFilled(false);
		scissor.setBorderPainted(false);
		scissor.setFocusPainted(false);

		instructions.setHorizontalAlignment(SwingConstants.CENTER);
		instructions.setFont(new Font("Verdana", Font.BOLD, 50));
		rock.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				chosen("rock");
			}
		});
		paper.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chosen("paper");
			}
		});
		scissor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chosen("scissor");
			}
		});
		gameMenuContainer.add(rock);
		gameMenuContainer.add(paper);
		gameMenuContainer.add(scissor);
		gameMenuContainer.setOpaque(false);
		gameMenu.setOpaque(false);
		gameMenu.setVisible(true);
		gameMenu.add(instructions, BorderLayout.NORTH);
		gameMenu.add(gameMenuContainer, BorderLayout.CENTER);
		try {
			rock.setIcon(new ImageIcon(ImageIO.read(new File("imgs/rock.png"))));
			paper.setIcon(new ImageIcon(ImageIO
					.read(new File("imgs/paper.png"))));
			scissor.setIcon(new ImageIcon(ImageIO.read(new File(
					"imgs/scissor.png"))));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Pre-builds the results of the user selected destroyer battle with the cpu
	 * selected destroyer
	 */
	public void buildWinner() {
		JLabel fightImg = new JLabel();
		JPanel winnerBottomContainer = new JPanel(new BorderLayout());
		JPanel winnerBottomContainerBottom = new JPanel(new BorderLayout());
		JButton playAgain = new JButton("Continue?");
		playAgain.setOpaque(false);
		playAgain.setBorderPainted(false);
		playAgain.setFocusPainted(false);
		playAgain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				gameText.setText("");

				if (Integer.parseInt(Link.localDataStore.getActiveGameElement()
						.getAttribute("currentRound")) < Integer
						.parseInt(Link.localDataStore.getActiveGameElement()
								.getAttribute("bestOf"))) {
					Link.localDataStore.getActiveGameElement().setAttribute(
							"currentRound",
							String.valueOf(Integer.parseInt(Link.localDataStore
									.getActiveGameElement().getAttribute(
											"currentRound")) + 1));

					roundStatus.setText("Round "
							+ Link.localDataStore
									.getAttributeOfActiveGameElement("currentRound")
							+ " of "
							+ Link.localDataStore
									.getAttributeOfActiveGameElement("bestOf"));

					gameScore.setText(Link.localDataStore.getScoreDisplay());

					winner.setVisible(false);
					gameMenu.setVisible(true);

					content.remove(winner);
					content.add(gameMenu, BorderLayout.CENTER);

				} else if (Integer.parseInt(Link.localDataStore
						.getActiveGameElement().getAttribute("currentRound")) == Integer
						.parseInt(Link.localDataStore.getActiveGameElement()
								.getAttribute("bestOf"))) {

					Link.localDataStore.getActiveGameElement().setAttribute(
							"ended", Link.time.toString());

					Link.localDataStore.determineAndSetGameWinner();

					roundStatus.setVisible(false);
					gameScore.setVisible(false);
					winner.setVisible(false);
					startMenu.setVisible(true);

					content.remove(winner);
					content.add(startMenu, BorderLayout.CENTER);
				}
			}
		});
		gameText.setHorizontalAlignment(SwingConstants.CENTER);
		gameText.setFont(new Font("Verdana", Font.BOLD, 30));
		gameText.setBorder(null);
		gameText.setOpaque(false);

		winnerText.setHorizontalAlignment(SwingConstants.CENTER);
		winnerText.setFont(new Font("Verdana", Font.BOLD, 30));
		winnerText.setBorder(null);
		winnerText.setOpaque(false);

		playAgain.setFont(new Font("Verdana", Font.BOLD, 30));

		winnerBottomContainerBottom.add(gameText, BorderLayout.NORTH);
		winnerBottomContainerBottom.add(playAgain, BorderLayout.SOUTH);

		winnerBottomContainerBottom.setOpaque(false);
		winnerBottomContainer.setOpaque(false);

		winnerBottomContainer.add(winnerText, BorderLayout.NORTH);
		winnerBottomContainer.add(winnerBottomContainerBottom,
				BorderLayout.SOUTH);
		cpuText.setHorizontalAlignment(SwingConstants.CENTER);
		cpuText.setFont(new Font("Verdana", Font.BOLD, 36));
		winnerTextContainer.add(cpuText);
		userText.setHorizontalAlignment(SwingConstants.CENTER);
		userText.setFont(new Font("Verdana", Font.BOLD, 36));
		winnerTextContainer.add(userText);
		userSelectedImg.setOpaque(false);
		cpuSelectedImg.setOpaque(false);
		winnerContainer.add(cpuSelectedImg);
		winnerContainer.add(fightImg);
		winnerContainer.add(userSelectedImg);
		winnerTextContainer.setOpaque(false);
		winnerContainer.setOpaque(false);
		winner.setOpaque(false);
		winner.setVisible(false);
		winner.add(winnerTextContainer, BorderLayout.NORTH);
		winner.add(winnerContainer, BorderLayout.CENTER);
		winner.add(winnerBottomContainer, BorderLayout.SOUTH);
		try {
			fightImg.setIcon(new ImageIcon(ImageIO.read(new File(
					"imgs/fight.png"))));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The starting method for the game to start processing and work.
	 */
	public RPSGame() {
		setLayout(new BorderLayout());
		setMinimumSize(new Dimension(800, 600));

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {

				System.out.println("Waiting for data save operation");

				Link.timer.cancel();

				new SwingWorker() {
					@Override
					protected Object doInBackground() throws Exception {

						Thread.sleep(1000);

						System.out.println("Exiting");

						System.exit(0);

						return null;
					}

				}.execute();

			}
		});

		try {
			content = new BackgroundPanel(new BorderLayout(),
					ImageIO.read(new File("imgs/bg.png")));
			setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
					ImageIO.read(new File("imgs/mouse.png")), new Point(8, 6),
					"Game Cursor"));
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null,
					"RPSGame was unable to find images",
					"Alert - Images Not Found", JOptionPane.WARNING_MESSAGE);
			e1.printStackTrace();
		}

		buildContinueSavedGame();
		buildStartMenu();
		buildGameMenu();
		buildWinner();
		buildStats();

		roundStatus.setOpaque(false);
		roundStatus.setHorizontalAlignment(SwingConstants.RIGHT);
		roundStatus.setFont(new Font("Verdana", Font.BOLD, 14));
		roundStatus.setVisible(false);

		gameScore.setOpaque(false);
		gameScore.setHorizontalAlignment(SwingConstants.RIGHT);
		gameScore.setFont(new Font("Verdana", Font.BOLD, 14));
		gameScore.setVisible(false);

		footer.setOpaque(false);
		footer.setHorizontalAlignment(SwingConstants.CENTER);
		footer.setFont(new Font("Verdana", Font.BOLD, 12));

		gameTopStatus.setOpaque(false);

		gameTopStatus.add(roundStatus, BorderLayout.EAST);
		gameTopStatus.add(gameScore, BorderLayout.WEST);

		content.add(gameTopStatus, BorderLayout.NORTH);
		content.add(footer, BorderLayout.SOUTH);

		add(content, BorderLayout.CENTER);
		setBounds((screenSize.width / 2) - 400, (screenSize.height / 2) - 300,
				800, 600);
		setVisible(true);
		
	}
}