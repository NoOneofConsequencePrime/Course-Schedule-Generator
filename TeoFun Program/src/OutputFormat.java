public class OutputFormat implements Comparable<OutputFormat> {
	private String outputText;
	private double instructorRating;
	
	public OutputFormat(String outputText, double instructorRating) {
		this.outputText = outputText;
		this.instructorRating = instructorRating;
	}
	
	public String getOutputText() {
		return outputText;
	}
	public double getInstructorRating() {
		return instructorRating;
	}
	
	@Override
	public int compareTo(OutputFormat other) {
		return Double.compare(other.instructorRating, instructorRating);
	}
}