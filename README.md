
# TimeTable Generator Using Genetic Algorithm

This Project uses Genetic Algorithm to generate a Collision-less Dynamic TimeTable.

Genetic Algorithms(GAs) are adaptive heuristic search algorithms that belong to the larger part of evolutionary algorithms. Genetic algorithms are based on the ideas of natural selection and genetics. These are intelligent exploitation of random search provided with historical data to direct the search into the region of better performance in solution space. They are commonly used to generate high-quality solutions for optimization problems and search problems.

Genetic algorithms simulate the process of natural selection which means those species who can adapt to changes in their environment are able to survive and reproduce and go to next generation. In simple words, they simulate “survival of the fittest” among individual of consecutive generation for solving a problem. Each generation consist of a population of individuals and each individual represents a point in search space and possible solution. Each individual is represented as a string of character/integer/float/bits. This string is analogous to the Chromosome.

Selection - Individuals in a population are selected for reproduction according to their fitness values. In biology, fitness is the number of offspring that survive to reproduction. Given a population consisting of individuals identified by their chromosomes, selecting two chromosomes as parents to reproduce offspring is guided by a probability rule that the higher the fitness an individual has, the more likely the individual is selected. There are many selection methods available including weighted roulette wheel, sorting schemes, proportionate reproduction, and tournament selection.

Crossover - Selected parents reproduce the offspring by performing a crossover operation on the chromosomes (cut and splice pieces of one parent to those of another). In nature, crossover implies two parents exchange parts of their corresponding chromosomes. In genetic algorithms, crossover operation makes two strings swap their partial strings. Since more fit individuals have a higher probability of producing offspring than less fit ones, the new population will possess on average an improved fitness. The basic crossover is a one-point crossover. Two selected strings create two offspring strings by swapping the partial strings, which are cut by one randomly sampled breakpoint along the chromosome. The one-point crossover can be easily extended to k-point crossover. It randomly samples k breakpoints on chromosomes and then exchanges every second corresponding segments of two parent strings.

Mutation - Mutation is an insurance policy against lost bits. It works on the level of string bits by randomly altering a bit value. With small probability, it randomly selects one bit on a chromosome then inverts the bit from 0 to 1 or vice versa. The operation is designed to prevent GA from premature termination, namely converging to a solution too early.

Elitism - The selection and crossover operators will tend to ensure that the best genetic material and the components of the fittest chromosomes will be carried forward to the next generation. However, the probabilistic nature of these operators implies that this will not always be the case. An elitist strategy is therefore required to ensure that the best genetic material will not be lost by chance. This is accomplished by always carrying forward the best chromosome from one generation to the next.


## Running the project

To run this project, you will need to Clone the Github Repo or download it from Github

    git clone github-repo-link


or you can download the project from the github in the zip format

After downloading the project either from command-line or Github,you have to open the project through Android studio,make sure you have the latest version of the Android studio,dependencies should be up to date and a suitable android emulator should be installed,After that run the project using the play button an instance of the emulator will pop-up running the app.
## Usage

The Project contains input.txt which consists of the data to be placed in the table/UI.It is placed inside app/src/main/res/raw/input.txt , you can alter the data to be placed inside the Algorithm and can edit it according to your own data.


Values that can be altered in the project 
1. Crossover rate
2. Population size
3. Gene size
4. Hours/day
5. days/week
6. Input.txt fil
## Definitions

The crossover rate in a genetic algorithm (GA) is a parameter that determines the probability of two parent individuals exchanging genetic information to create offspring.

Population size in a genetic algorithm is like the number of people in a village. It determines how many individuals (potential solutions) exist in each generation of the algorithm

Gene size in a genetic algorithm is like the length of a recipe's ingredient list. It refers to how many components or characteristics make up an individual solution.

Input File consists of the input data to be inserted into the application



## Future Scope
The Algorithm stops when the best-fitness value is found irrespective of the left-over generations,so we will never know whether the left-out generations could have made a better result/outcome.Future work includes solving this issue


## Authors

- https://github.com/pranavkhurana/Time-table-scheduler (Reference)

- https://github.com/Hames-1616 (Shah Haamid)

