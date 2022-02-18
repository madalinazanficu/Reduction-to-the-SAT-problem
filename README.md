### Zanficu Madalina-Valentina, Grupa: 323CA
# **Tema Analiza Alogoritmilor - Reduceri polinomiale la problema SAT**


### *Task1 - Retele sociale*
**Problema k-clique**
#### Pentru rezolvarea problemei k-clique sunt parcurse 3 etape:
#### Aceste etape genereaza clauzele necesare oracolului.
- Etapa 1: fiecarui element din clique ii este asignat un nod.
Astfel pentru  fiecare element din clique, din intervalul [1,k],
am considerat ca oricare nod se poate afla pe orice pozitie din clique.

- Etapa 2: pentru oricare 2 noduri care nu au legatura
=> **persoanele nu fac parte din aceeasi retea sociala**
=> nu este posibila adaugarea acestora in acelasi timp in clique.

- Etapa 3: fiecarui element din clique, ii corespunde un singur nod.
**Astfel fiecare persoana poate sa ocupe o singura pozitie in grupul de persoane.**

#### **Codificarea termenilor**:
- Fiecarui termen ii corespunde 2 indici:
    - x = pozitia elementului in clique (grupul social)
    - y = numarul nodului aferent (persoana)
- Pentru codificarea termenilor, am ales o functie de codificare Cantor
  hash function
    - (x + y) * (x + y + 1) / 2 + x
- Pentru decodificarea termenilor:
    - In prima etapa cand sunt realizate toate combinatiile de codificari,
      am mapat fiecare cheie cu numarul de ordine al persoanei ce detine
      cheia respectiva.
    - La descifrarea oracolului, se va cauta valoarea cheii in hashmap.

#### **Complexitate**
- In metoda formulateOracleQuestions, for-urile ofera complexitatea generala a algoritmului:
- O(k * n) + O (k * k * n * n) + O (n * k * k) => **O (n ^ 2 * k ^ 2)**;


### *Task2 - Reclame buclucase*
**Problema acoperirii cu varfuri**
- Prespune determinarea unui numar minim de noduri k, care garanteaza
ca toate legaturile de graf contin cel putin un nod in grupul format
din cele k elemente.
- Grupul de k elemente corespunde retelei sociale din enunt.
- Astfel selectand aceste persoane avem acces la toti membrii din retea.


#### Pentru determinarea numarului minim k, astfel incat raspunsul problemei sa fie True, am considerat k din intrevalul [1, n] si am testat algoritmul pe fiecare valoarea din interval, pana cand rezultatul a fost True.

#### Pentru rezolvarea problemei acoperirii cu varfuri, sunt parcurse 4 etape pentru generarea clauzelor.

- Etapa 1: pentru fiecare element al acoperirii, au fost considerate in
  prima instanta toate nodurile din graf.

- Etapa 2: un nod nu poate ocupa mai multe pozitii in acoperire
  => poate fi considerat o singura data in acoperire

- Etapa 3: pentru orice legatura dintre doua noduri, exista cel putin
  un nod in acoperire => pot fi chiar ambele noduri

- Etapa 4: fiecare pozitie a acoperirii corespunde unui singur nod
  => doua noduri nu pot avea aceeasi pozitie in vertex cover

#### **Complexitate**
- Metoda formulateOracleQuestion este apelata in solve de maxim n ori,
  pentru a determina elementul minim k.
- Complexitate formulateOracleQuestion:
  O(k * n) + O (n * k * k) + O (n * n * k) + O(k * n * n)
- **Complexitate generala: O (n ^ 3 * k)**

#### **Codificarea termenilor**:
- Fiecarui termen ii corespunde 2 indici:
    - x = numarul nodului aferent (persoana)
    - y = pozitia elementului din vertex cover

- Pentru codificare si decodificare s-a optat pentru aceeasi functie de hash
  si pentru utilizarea unui hashmap, unde cheia este valoarea codificata a
  teremenului iar valoarea este numarul nodului. 

### *Task3 - Alocarea registrilor*
**Problema colorarii grafului**
- Prespune colorarea nodurilor unui graf cu cel mult k culori,
  altfel incat pentru 2 noduri conectate, culorile acestora trebuie sa difere.

- Asocierea cu registrele: fiecare operand utilizat pentru obtinerea unui calcul,
  trebuie stocat intr-un registru, astfel calulele sunt realizate direct si mai
  rapid de catre procesor, fara a mai acceasa de fiecare data memoria.

- Avem la dispozitie un numar limitat de registre, solutia determina daca
  registre sunt suficienti, si in acest caz, in ce registru se afla fiecare operand

#### Etapele generarii clauzelor
- Etapa 1: fiecare operand (nod) trebuie sa existe intr-un registru

- Etapa 2: fiecare operand (nod) se gaseste intr-un singur registru
           => un operand nu poate ocupa 2 registre simultan

- Etapa 3: pentru fiecare calcul ce trebuie efectuat (exista muchie intre noduri)
           operanzii respectivi trebuie sa se gaseasca in registre diferite
           pentru a putea face posibila efectuarea calculului.

#### **Complexitate**
- O(n * k) + O(n * k * k) + O(n * n * k) => **O(n ^ 2 * k)**