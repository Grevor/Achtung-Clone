package controller;

public interface Controller {
	/**
	 * Checks if the controller has been told to turn clockwise.
	 * @return
	 * True if it has, else false.
	 */
	public boolean turnClockwise();
	/**
	 * Checks if the controller has been told to turn counter-clockwise.
	 * @return
	 * True if it has, else false.
	 */
	public boolean turnCounterClockwise();
	/**
	 * Checks if the controller's value is valid.
	 * @return
	 * True if it has, else false.
	 */
	public boolean isValid();
	
	/**
	 * Checks if the pause/select-button is currently clicked.
	 * @return
	 * True if it is, else false.
	 */
	public boolean pauseAndSelectButton();
	/**
	 * Checks if the back-button has been pressed since the last frame.
	 * @return
	 */
	public boolean backButton();
}
