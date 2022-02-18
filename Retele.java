import java.io.*;
import java.util.*;

class Retele extends Task {
    private Integer n;
    private Integer m;
    private Integer k;
    private final HashMap<Integer, List<Integer>> connections;
    private String problemAnswer;
    private final List<Integer> oracleAnswer;
    private final HashMap<Integer, Integer> keys;

    public Retele() {
        this.connections = new HashMap<>();
        this.problemAnswer = null;
        this.oracleAnswer = new ArrayList<>();
        this.keys = new HashMap<>();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Retele retele = new Retele();
        retele.solve();
    }

    @Override
    public void solve() throws IOException, InterruptedException {
        readProblemData();
        formulateOracleQuestion();
        askOracle();
        decipherOracleAnswer();
        writeAnswer();
    }

    @Override
    public void readProblemData() throws IOException {
        Scanner inputData = new Scanner(System.in);
        this.n = inputData.nextInt();
        this.m = inputData.nextInt();
        this.k = inputData.nextInt();

        int i;
        for (i = 0; i < this.m; i++) {
            Integer u = inputData.nextInt();
            Integer v = inputData.nextInt();

            // Graful este neorientat =>
            // nodul u este vecin cu nodul v
            addNeighbour(u, v);

            // nodul v este vecin cu noul u
            addNeighbour(v, u);
        }
    }

    @Override
    public void formulateOracleQuestion() throws IOException {
        // se defineste buffered writer-ul ce va fi transmis oracolului
        BufferedWriter writer = new BufferedWriter(new FileWriter("./sat.cnf"));

        // se defineste string-ul ce va contine clauzele
        StringBuilder request = new StringBuilder();
        int i, j, u, v;


        // Etapa 1:
        // Initial pentru fiecare clica sunt considerate toate nodurile
        // fiecare termen are asociat un numar unic
        // calculat cu ajutorul functiei Cantor pairing function
        for (i = 1; i <= this.k; i++) {
            for (j = 1; j <= this.n; j++) {
                Integer key = cantorHashFunction(i, j);
                keys.put(key, j);
                request.append(key).append(" ");
            }
            request.append("0\n");
        }

        // Etapa 2:
        // Pentru oricare doua noduri care nu au muchie intre ele
        // nu este posibila adaugarea lor simultan in clica
        for (i = 1; i <= this.k; i++) {
            for (j = 1; j <= this.k; j++) {
                if (i != j) {
                    // se cauta toate nodurile care nu sunt vecine
                    for (u = 1; u <= this.n; u++) {
                        for(v = u + 1; v <= this.n; v++) {
                            if (!connections.containsKey(u) || !(connections.get(u).contains(v))) {
                                // valoarea unica asociata termenului X_iu
                                Integer key1 = cantorHashFunction(i, u);
                                // valoarea unica asociata termenului X_jv
                                Integer key2 = cantorHashFunction(j, v);

                                request.append("-").append(key1).append(" -")
                                                    .append(key2).append(" 0\n");
                            }
                        }
                    }
                }
            }
        }

        // Etapa 3: oricare nod poate sa apara pe o singura pozitie in clica
        for (u = 1; u <= this.n; u++) {
            for (i = 1; i <= this.k; i++) {
                for (j = i + 1; j <= this.k; j++) {
                    Integer key1 = cantorHashFunction(i, u);
                    Integer key2 = cantorHashFunction(j, u);

                    request.append("-").append(key1).append(" -").append(key2).append(" 0\n");
                }
            }
        }

        // Determinarea numarului total de clauze
        int numLines = request.toString().split("\n").length;


        // Scrierea datelor in fisierul de input necesar oracolului
        try {
            writer.write("p cnf " + n * n + " " + numLines + "\n" + request);
        } catch (IOException e1) {
            writer.write("Error");
        } finally {
            writer.close();
        }
    }

    @Override
    public void decipherOracleAnswer() throws IOException {
        Scanner data = new Scanner(new File("./sat.sol"));
        String line = data.nextLine();
        int i;

        // se extrage raspunsul boolean al problemei
        this.problemAnswer = line;

        if (Objects.equals(line, "True")) {
            int nr = data.nextInt();
            for (i = 0; i < nr; i++) {
                int x = data.nextInt();

                // se extrag nodurile care fac parte din clica
                if (x > 0) {
                    this.oracleAnswer.add(keys.get(x));
                }
            }
        }
    }

    @Override
    public void writeAnswer() throws IOException {
        System.out.println(problemAnswer);
        if (problemAnswer.equals("True")) {
            for (Integer element : oracleAnswer) {
                System.out.print(element + " ");
            }
        }
    }

    /**
     * @param x apartine [1, k]
     * @param y apartine [1, n]
     * @return the hased value
     */
    public Integer cantorHashFunction(Integer x, Integer y) {
        return (x + y) * (x + y + 1) / 2 + x;
    }

    public void addNeighbour(Integer u, Integer v) {
        List<Integer> entry;
        // daca nodul u exista deja in lista de conexiuni => se adauga vecinul v
        if (connections.containsKey(u)) {
            entry = connections.get(u);
            entry.add(v);
        } else {
            // altfel creez o intrare in hashmap pentru nodul u si adaug vecinul v
            entry = new ArrayList<>();
            entry.add(v);
            connections.put(u, entry);
        }
    }
}