/**
 * 
 */
package com.avis.gol.main;

import java.util.Scanner;

public class GameOfLife {
	static int univSize;
	int[] universe;
	int[] universeNew;

	public static void main(String[] args) {
		GameOfLife gol = new GameOfLife();
		gol.makeInitialConfig();
		gol.displayUniverse();
		for (int i = 1; i < 10; i++) {
			System.out.println("*************** Generation Number = " + i	+ " **************");
			gol.evolution();
			gol.displayUniverse();
		}

	}

	/**
	 * method to get input from user and make the initial state of the universe
	 */
	private void makeInitialConfig() {
		Scanner sc = new Scanner(System.in);
		System.out
				.println("Enter the size of Universe, a number greater than 3 (eg:10)");
		String input = sc.nextLine();
		try {
			univSize = Integer.parseInt(input);
		} catch (NumberFormatException nfe) {
			System.out.println("Invalid input");
		}
		if (univSize < 4) {
			System.out.println("Invalid input");
		} else {
			System.out
					.println("Enter space seperated numbers to specify the life existing locations");
			System.out.println("Enter numbers only less than "
					+ (univSize * univSize + 1));
			String lifeString = sc.nextLine();
			sc.close();
			String[] lifeLocations = lifeString.split(" ");
			universe = new int[univSize * univSize];
			universeNew = new int[univSize * univSize];
			for (int i = 0; i < lifeLocations.length; i++) {
				if (!(Integer.parseInt(lifeLocations[i]) > universe.length)) {
					universe[Integer.parseInt(lifeLocations[i]) - 1] = 1;
				}
			}
		}
	}

	/**
	 * method to display the current view of the universe
	 */
	private void displayUniverse() {
		int i = 0;
		for (int row = 1; row <= univSize; row++) {
			for (int col = 1; col <= univSize; col++) {
				System.out.print(universe[i + col - 1] + "\t");
				if (col == univSize) {
					i = i + col;
					System.out.println();
				}
			}
		}
	}

	/**
	 * do a tick of the system clock or evolution of the universe
	 */
	private void evolution() {
		System.arraycopy(universe, 0, universeNew, 0, univSize * univSize);
		for (int i = 0; i < universe.length; i++) {
			Neighbour neighbours = getNeighboursData(i);
			if (universe[i] == 1) {
				if (neighbours.count < 2) {
					universeNew[i] = 0; // die due to under population
				} else if (neighbours.count > 3) {
					universeNew[i] = 0; // die due to over population
				}
			} else if (neighbours.count == 3) {
				universeNew[i] = 1; // become live
			}
		}
		System.arraycopy(universeNew, 0, universe, 0, univSize * univSize);
	}

	/**
	 * to get the neighbours for the given location
	 * @param location
	 * @return Neighbour - Neighbours data of the location
	 */
	private Neighbour getNeighboursData(int location) {
		location = location + 1; // to make it 1-index from 0-index
		Neighbour neighboursData = null;

		if (location % univSize == 0) { // if location is at the right end of
										// the universe
			int[] neighbourLocs = { location - 1, location + univSize - 1,
					location + univSize, location - univSize - 1,
					location - univSize };
			neighboursData = makeNeighbourFromLocs(neighbourLocs);
		} else if (location % univSize == 1) { // if location is at the left end
												// of the universe
			int[] neighbourLocs = { location + 1, location + univSize,
					location + univSize + 1, location - univSize + 1,
					location - univSize };
			neighboursData = makeNeighbourFromLocs(neighbourLocs);
		} else if (location <= univSize) { // if location is at the beginning of
											// the universe
			int[] neighbourLocs = { location - 1, location + 1,
					location + univSize - 1, location + univSize,
					location + univSize + 1 };
			neighboursData = makeNeighbourFromLocs(neighbourLocs);
		} else {
			int[] neighbourLocs = { location - univSize - 1,
					location - univSize, location - univSize + 1, location - 1,
					location + 1, location + univSize - 1, location + univSize,
					location + univSize + 1 };
			neighboursData = makeNeighbourFromLocs(neighbourLocs);
		}
		return neighboursData;
	}

	/**
	 * To get the neighbours data
	 * @param neighbourLocs
	 * @return
	 */
	private Neighbour makeNeighbourFromLocs(int[] neighbourLocs) {
		Neighbour neighboursData = new Neighbour();
		// count the number of valid locations i.e., locations greater than 0
		// and less than universe whole size
		int validLocsLength = getValidNeighboursSize(neighbourLocs);
		// create the valid location numbers
		int[] validNeighbourLocs = createValidLocs(neighbourLocs,
				validLocsLength);
		int[] neighbourLocValues = new int[validNeighbourLocs.length];

		int count = 0;
		for (int i = 0; i < validNeighbourLocs.length; i++) {
			count = count + universe[validNeighbourLocs[i] - 1];
			neighbourLocValues[i] = universe[validNeighbourLocs[i] - 1];
		}
		neighboursData.count = count;
		neighboursData.neighbourLocs = validNeighbourLocs;
		return neighboursData;
	}

	/**
	 * Method to get the count of valid neighbours
	 * 
	 * @param neighbourLocs
	 * @return neighbours count
	 */
	private int getValidNeighboursSize(int[] neighbourLocs) {
		int validLocsSize = 0;
		for (int i = 0; i < neighbourLocs.length; i++) {
			if (neighbourLocs[i] > 0 && neighbourLocs[i] <= universe.length) {
				validLocsSize++;
			}
		}
		return validLocsSize;
	}

	/**
	 * Method to get the valid locations of the neighbours
	 * 
	 * @param neighbourLocs
	 *            neighbour locations
	 * @param validLocsSize
	 *            neighbours count
	 * @return valid neighbour locations
	 */
	private int[] createValidLocs(int[] neighbourLocs, int validLocsSize) {
		int[] validNeighbourLocs = new int[validLocsSize];
		for (int i = 0, validIndex = 0; i < neighbourLocs.length; i++) {
			if (neighbourLocs[i] > 0 && neighbourLocs[i] <= universe.length) {
				validNeighbourLocs[validIndex] = neighbourLocs[i];
				validIndex++;
			}
		}
		return validNeighbourLocs;
	}

	/**
	 * Inner class to maintain Neighbours data
	 * 
	 * @author Shivi
	 * 
	 */
	public class Neighbour {
		int count;
		int[] neighbourLocs;
	}

}
