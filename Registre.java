import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class Registre extends Task {
    // numarul total de noduri
    private Integer n;

    // numarul de legaturi dintre noduri
    private Integer m;

    // numarul de registre disponibile
    private Integer k;

    // legaturile dintre noduri
    private final HashMap<Integer, List<Integer>> connections;

    // raspunsul problemei => True / False
    private String problemAnswer;

    // raspunsul oracolului (contine n elemente) oracleAnswer[i] = registrul aferent variabilei
    private final List<Integer> oracleAnswer;

    public Registre() {
        this.connections = new HashMap<>();
        this.oracleAnswer = new ArrayList<>();
        this.problemAnswer = null;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Registre registre = new Registre();
        registre.solve();
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
        // se citesc cele m legaturi dintre registre
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


        // Etapa 1: fiecare nod se va regasi intr-un registru
        for (i = 1; i <= this.n; i++) {
            for (j = 1; j <= this.k; j++) {
                Integer key = cantorHashFunction(i, j);
                request.append(key).append(" ");
            }
            request.append("0\n");
        }

        // Etapa 2: un nod se afla intr-un singur registru
        // Un nod nu poate fi considerat in 2 registre simultan
        for (u = 1; u <= this.n; u++) {
            for (i = 1; i <= this.k; i++) {
                for (j = i + 1; j <= this.k; j++) {
                    Integer key1 = cantorHashFunction(u, i);
                    Integer key2 = cantorHashFunction(u, j);

                    request.append("-").append(key1).append(" -").append(key2).append(" 0\n");
                }
            }
        }

        // Etapa 3: pentru fiecare legatura dintre 2 noduri
        // Nodurile respective trebuie plasate in registre diferiti
        for (u = 1; u <= this.n; u++) {
            for (v = u + 1; v <= this.n; v++) {
                for (i = 1; i <= this.k; i++) {
                    // daca intre 2 noduri exista legatura
                    // => nu pot fi plasate in acelasi registru
                    if (connections.get(u) != null && connections.get(u).contains(v)) {
                        Integer key1 = cantorHashFunction(u, i);
                        Integer key2 = cantorHashFunction(v, i);

                        request.append("-").append(key1).append(" -").append(key2).append(" 0\n");
                    }
                }
            }
        }

        // determinarea numarului total de clauze
        int numLines = request.toString().split("\n").length;


        // Screierea datelor in fisierul de input necesar oracolului
        try {
            writer.write("p cnf " + this.n * this.k + " " + numLines + "\n" + request);
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
        int i, j, u;

        // se extrage raspunsul boolean al problemei
        this.problemAnswer = line;

        int []temp = new int[10001];
        if (Objects.equals(line, "True")) {
            int nr = data.nextInt();
            for (i = 1; i <= nr; i++) {
                temp[i] = data.nextInt();
            }

            // se decodeaza raspunsul primit de la oracol
            for (u = 1; u <= this.n; u++) {
                for (j = 1; j <= this.k; j++) {
                  if (temp[cantorHashFunction(u, j)] > 0) {
                      oracleAnswer.add(j);
                  }
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
     * @param x apartine [1, n]
     * @param y apartine [1, k]
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