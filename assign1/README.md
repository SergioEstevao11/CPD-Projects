# CPD-SingleCorePerformance

FEUP's CPD first project whose objective is to test the impact of cache faults
in the performance of a single threaded progam.

## Usage

### C/C++ implementation

To compile and chack the execution times of the c/c++ implementation of
the different variants of the matrix multiplication algorithm, run the
following commands:

```sh
g++ -O2 matrixproduct.cpp -o matrixproduct -lpapi
```
```sh
./matrixproduct
```

**Note:** [PAPI](http://icl.cs.utk.edu/papi/) must be installed the system.

### Rust implementation

In order to check the execution times in the rust implementation of the
column and line variants of matrix multiplication algorithm, run the following 
commands:

```sh
cd src/Rustmatrixproduct
```
```sh
cargo run --release
```

## Contributors

- [Miguel Rodrigues](mailto:up201906042@edu.fe.up.pt)
- [Sérgio Estêvão](mailto:up201905680@edu.fe.up.pt)
- [Sofia Germer](mailto:up201907461@edu.fe.up.pt)

