use std::time::Instant;

fn mul_by_cols(m_len: usize) {
    let m_a: Vec<Vec<f32>> = vec![vec![1.0; m_len]; m_len];
    let mut m_b: Vec<Vec<f32>> = vec![];
    let mut m_c: Vec<Vec<f32>> = vec![vec![0.0; m_len]; m_len];

    for i in 0..m_len {
        m_b.insert(i, vec![(i+1) as f32; m_len]);
    }

    let start = Instant::now();

    for i in 0..m_len {
        for j in 0..m_len {
            for k in 0..m_len {
                m_c[i][j] += m_a[i][k] * m_b[k][j]
            }
        }
    }

    let elapsed = start.elapsed();
    println!("elapsed time [mul_by_cols]: {elapsed:.3?}");

    let elem : usize = m_len * (m_len+1)/2;
    assert_eq!(m_c[0][0], elem as f32);
    println!("passed arithmetic progression test: {elem} (m_len: {m_len})");
}

fn mul_by_line(m_len: usize) {
    let m_a: Vec<Vec<f32>> = vec![vec![1.0; m_len]; m_len];
    let mut m_b: Vec<Vec<f32>> = vec![];
    let mut m_c: Vec<Vec<f32>> = vec![vec![0.0; m_len]; m_len];

    for i in 0..m_len {
        m_b.insert(i, vec![(i+1) as f32; m_len]);
    }

    let start = Instant::now();

    for i in 0..m_len {
        for k in 0..m_len {
            for j in 0..m_len {
                m_c[i][j] += m_a[i][k] * m_b[k][j]
            }
        }
    }

    let elapsed = start.elapsed();
    println!("elapsed time [mul_by_line]: {elapsed:.3?}");

    let elem : usize = m_len * (m_len+1)/2;
    assert_eq!(m_c[0][0], elem as f32);
    println!("passed arithmetic progression test: {elem} (m_len: {m_len})");
}

fn main() {
    for len in (600..3400).step_by(400) {
        println!("Executing matrix multiplication algorithm for a length of {len}.");
        mul_by_cols(len);
        mul_by_line(len);
        println!();
    }
}
