package np.edu.kathford.www.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class UniqueNumberGenerator {
    private int range;
    private Set<Integer> integerSet = new HashSet<>();
    private Random random;


    public UniqueNumberGenerator(int range, int seed) {
        this.range = range;
        random = new Random(seed);
    }

    public int nextInt() throws Exception {
        if (integerSet.size() >= range) {
            throw new Exception("Out of range");
        }
        int rand;
        do {
            rand = random.nextInt(range);
        } while (integerSet.contains(rand));
        integerSet.add(rand);
        return rand;
    }
}


