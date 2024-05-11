import java.util.Random;

public class Autoassociator {
	private int weights[][];
	private int trainingCapacity;
	
	public Autoassociator(CourseArray courses) {	
		int numCourses = courses.length();
        weights = new int[numCourses][numCourses];
        Random rand = new Random();
        for (int i = 0; i < numCourses; i++) {
            for (int j = 0; j < numCourses; j++) {
                if (i != j) {
                    weights[i][j] = rand.nextBoolean() ? 1 : -1;
                    //weights[j][i] = weights[i][j];
                } else {
                    weights[i][j] = 0;
                }
            }
        }
        trainingCapacity = numCourses;
	}
	
	public int getTrainingCapacity() {
		return trainingCapacity;
	}
	
	public void training(int pattern[]) {
		for (int i = 0; i < pattern.length; i++) {
            for (int j = i; j < pattern.length; j++) {
                if (i != j) {
                    weights[i][j] += pattern[i] * pattern[j];
                    //weights[j][i] = weights[i][j];
                }
            }
        }
	}
	
	public int unitUpdate(int neurons[]) { //neurons = pattern
		Random rand = new Random();
        int index = rand.nextInt(neurons.length);
        int sum = 0;
        for (int i = 0; i < neurons.length; i++) {
            sum += weights[index][i] * neurons[i];
        }
		neurons[index] = sum >= 0 ? 1 : -1;
        return index;
	}
	
	public void unitUpdate(int neurons[], int index) { //neurons = pattern
		int sum = 0;
        for (int i = 0; i < neurons.length; i++) {
            sum += weights[index][i] * neurons[i];
        }
        neurons[index] = sum >= 0 ? 1 : -1;
	}
	
	public void chainUpdate(int neurons[], int steps) {
		for (int s = 0; s < steps; s++) {
            unitUpdate(neurons); //Should the updates be done on random neurons?
        }
	}
	
	public void fullUpdate(int neurons[]) {
		boolean stable = false;
        while (stable != true) {
            int[] prevNeurons = neurons.clone();
            for (int i = 0; i < neurons.length; i++) {
                int sum = 0;
                for (int j = 0; j < neurons.length; j++) {
                    sum += weights[i][j] * prevNeurons[j];
                }
                neurons[i] = sum >= 0 ? 1 : -1;
            }
            stable = java.util.Arrays.equals(prevNeurons, neurons);
        }
	}
}
