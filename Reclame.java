import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class Reclame extends Task {
    // numarul total de noduri
    private Integer n;

    // numarul total de legaturi dintre noduri
    private Integer m;

    // numarul minim de elemente pentru formararea unei acoperiri
    private Integer k = 0;

    // legaturile dintre noduri
    private HashMap<Integer, List<Integer>> connections;

    // raspunsul problemei => True / False
    private String problemAnswer;

    private List<Integer> oracleAnswer;

    // fiecare raspuns codificat realizat cu cantorHash corespunde unui nod
    private final HashMap<Integer, Integer> keys = new HashMap<>();


    public static void main(String[] args) throws IOException, InterruptedException {
        Reclame reclame = new Reclame();
        reclame.solve();
    }

    @Override
    public void solve() throws IOException, InterruptedException {
        int i;
        readProblemData();
        for (i = 1; i <= this.n; i++) {
            this.k = i;
            formulateOracleQuestion();
            askOracle();
            decipherOracleAnswer();
            if (this.problemAnswer.equals("True") && this.oracleAnswer.size() == this.k) {
                writeAnswer();
                break;
            }
        }
    }

    @Override
    public void readProblemData() throws IOException {
        Scanner inputData = new Scanner(System.in);
        this.n = inputData.nextInt();
        this.m = inputData.nextInt();

        int i;
        this.connections = new HashMap<>();
        // se citesc cele m legaturi dintre noduri
        for (i = 0; i < this.m; i++) {
            Integer u = inputData.nextInt();
            Integer v = inputData.nextInt();

            addNeighbour(u, v);
        }
    }

    @Override
    public void formulateOracleQuestion() throws IOException {
        // se defineste buffered writer-ul ce va fi transmis oracolului
        BufferedWriter writer = new BufferedWriter(new FileWriter("./sat.cnf"));

        // se defineste string-ul ce va contine clauzele
        StringBuilder request = new StringBuilder();
        int i, j, u, v;

        // Etapa 1: se determina toate elementele posibile ale acoperirii
        // acoperirea are k elemente
        for (i = 1; i <= this.k; i++) {
            // pentru fiecare element al acoperirii, consider toate nodurile
            for (j = 1; j <= this.n; j++) {
                Integer key = cantorHashFunction(j, i);
                keys.put(key, j);
                request.append(key).append(" ");
            }
            request.append("0\n");
        }

        // Etapa 2:
        // Fiecare nod poate fi considerat cel mult o data in acoperire
        for (u = 1; u <= this.n; u++) {
            for (i = 1; i <= this.k && i + 1 <= this.k; i++) {
                Integer key1 = cantorHashFunction(u, i);
                request.append("-").append(key1).append(" ");

                for (j = i + 1; j <= this.k; j++) {
                    Integer key2 = cantorHashFunction(u, j);
                    request.append("-").append(key2).append(" ");
                }
                request.append("0\n");
            }
        }


        // Etapa 3:
        // Pentru fiecare legatura dintre noduri, exista cel putin un nod in acoperire
        for (u = 1; u <= this.n; u++) {
                for (v = u + 1; v <= this.n; v++) {

                    // daca intre noduri exista muchie
                    if (connections.get(u) != null && connections.get(u).contains(v)){
                        // le plasez in toate acoperirile posibile
                        for (i = 1; i <= this.k; i++) {
                            Integer key1 = cantorHashFunction(u, i);
                            request.append(key1).append(" ");
                        }
                        for (i = 1; i <= this.k; i++) {
                            Integer key2 = cantorHashFunction(v, i);
                            request.append(key2).append(" ");
                        }
                        request.append("0\n");
                    }
                }
        }

        // Etapa 4: pentru o poztie specifica in vertex cover, exista un unic nod
        // Doua noduri nu pot avea aceeasi pozitie in vertex cover
          for (i = 1; i <= this.k; i++) {
              for (u = 1; u <= this.n; u++) {
                  for (v = u + 1; v <= this.n; v++) {
                      Integer key1 = cantorHashFunction(u, i);
                      Integer key2 = cantorHashFunction(v, i);
                      request.append("-").append(key1).append(" -").append(key2).append("  0\n");
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
        int i;

        // se extrage raspunsul boolean al problemei
        this.problemAnswer = line;

        // raspunsul este afirmativ
        if (Objects.equals(line, "True")) {
            this.oracleAnswer = new ArrayList<>();
            int nr = data.nextInt();
            for (i = 0; i < nr; i++) {
                int x = data.nextInt();
                // se extrag nodurile din acoperire
                if (x > 0) {
                    this.oracleAnswer.add(keys.get(x));
                }
            }
        } else {
            this.oracleAnswer = null;
        }
    }

    @Override
    public void writeAnswer() throws IOException {
        if (oracleAnswer != null) {
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