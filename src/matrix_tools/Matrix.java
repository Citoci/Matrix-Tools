package matrix_tools;

import static java.lang.Math.pow;
import static java.lang.Math.abs;

public class Matrix {
    private final int NUM_ROWS, NUM_COLS;
    private final boolean isSquare;
    private double [][] matrix;
    
    public Matrix(int _NUM_ROWS, int _NUM_COLS){
        NUM_ROWS = _NUM_ROWS;
        NUM_COLS = _NUM_COLS;
        matrix = new double[NUM_ROWS][NUM_COLS];
        if(NUM_COLS==NUM_ROWS) isSquare = true;
        else isSquare = false;
    }

    public int getNumRows(){ return NUM_ROWS; }
    
    public int getNumCols(){ return NUM_COLS; }
    
    public double getVal(int r, int c){ return matrix[r-1][c-1]; }
    
    public void setVal(double val, int r, int c){ matrix[r-1][c-1] = val; }
    
    /**
     * @return ordered array of values (per righe) 
     */    
    public double[] getVals(){
        double[] vals = new double[NUM_ROWS*NUM_COLS];
        int i=0;
        for(int r=0; r<NUM_ROWS; r++)
            for(int c=0; c<NUM_COLS; c++, i++)
                vals[i] = matrix[r][c];
        return vals;
    }
    
    /**
     * set all the values
     * @param vals ordered array of values (per righe)
     * @param offset index of first value to set
     * @return this matrix instane
     */
    public Matrix setValuesFrom(double[] vals, int offset){
        int i=0, r=0, c=0;
        
        // set rows offset
        while(offset>NUM_COLS){
            r += NUM_COLS;
            offset -= NUM_COLS;
        }
        c += offset;
        
        // the c=0 is there so the offset is kept for the first cycle
        for(; r<NUM_ROWS; r++, c=0)
            for(; c<NUM_COLS && i<vals.length; c++, i++)
                matrix[r][c] = vals[i];            
        return this;
    }
    
    /**
     * calls setValuesFrom() method with 0 as offset
     * @param vals ordered array of values (per righe)
     * @return this matrix instance
     */
    public Matrix setValues(double[] vals){ return setValuesFrom(vals, 0); }
    
    /**
     * @return the Traspost Matrix 
     */
    public Matrix getTraspost(){
        double[] vals = new double[NUM_ROWS*NUM_COLS];
        int i=0;
        for(int c=0; c<NUM_COLS; c++)
            for(int r=0; r<NUM_ROWS; r++, i++)
                vals[i] = matrix[r][c];
        return (new Matrix(NUM_COLS, NUM_ROWS)).setValues(vals);
    }
    
    public Matrix getInvers(){
    	double[] vals = new double[NUM_ROWS*NUM_COLS];
    	for(int r=0, i=0; r<NUM_ROWS; r++){
    		for(int c=0; c<NUM_COLS; c++, i++){
    			vals[i] = pow(-1, r+c)*(this.dropRow(r+1).dropCol(c+1).getDeterminant())/getDeterminant();
    			if(abs(vals[i])<0.000001) vals[i]=0;
    		}
    	}
    	return (new Matrix(NUM_ROWS, NUM_COLS)).setValues(vals).getTraspost();
    }
    
    /**
     * sum this matrix to another
     * @param m2 the other
     * @return the result matrix
     */
    public Matrix sum(Matrix m2){
        if(NUM_ROWS!=m2.getNumRows() || NUM_COLS!=m2.getNumCols()){
            System.out.println("CANT SUM TWO MATRIXS DIVERSES");
            return null;
        }
        double[] vals = getVals(), vals2 = m2.getVals();
        for(int i=0; i<(NUM_ROWS*NUM_COLS); i++)
            vals[i] += vals2[i];
        return (new Matrix(NUM_ROWS, NUM_COLS)).setValues(vals);
    }
    
    /**
     * Multipication for Scalar
     * @param k double to multiply the matrix for
     * @return
     */
    public Matrix multiply(double k){
    	double[] vals = getVals();
    	for(int i=0; i<NUM_ROWS*NUM_COLS; i++)
    		vals[i] *= k;
    	return (new Matrix(NUM_ROWS, NUM_COLS)).setValues(vals);    			
    }
    
    /**
     * Multiplication row for colomns
     * @param m2 Matrix to multiply for
     */
    public Matrix multiply(Matrix m2){
    	if(NUM_COLS != m2.getNumRows()){
    		System.out.println("CANT MULTIPLY THIS MATRIX BECAUS THEY HAVE DIVERSES NUMS OF ROW AND COLS");
    		return null;
    	}
    	
    	double[] m3Vals = new double[NUM_ROWS*m2.getNumCols()];
    	for(int r=0, i=0; r<NUM_ROWS; r++)
    		for(int c=0; c<m2.getNumCols(); c++, i++){
    			double sum=0;
    			for(int j=0; j<NUM_COLS; j++)
    				sum += getVal(r+1, j+1)*m2.getVal(j+1, c+1);
    			m3Vals[i] = sum;
    		}	
    	return (new Matrix(NUM_ROWS, m2.getNumCols())).setValues(m3Vals);
    }
    
    /**
     * create a matrix dropping a specific row 
     * @param rowToDrop index of row to drop
     * @return the modified matrix
     */
    private Matrix dropRow(int rowToDrop){
        rowToDrop--;
        double[] vals = new double[(NUM_ROWS-1)*NUM_COLS];
        int i=0;
        for(int r=0; r<NUM_ROWS; r++)
            for(int c=0; c<NUM_COLS && r!=rowToDrop; c++, i++)
                vals[i] = matrix[r][c];
        return (new Matrix(NUM_ROWS-1, NUM_COLS)).setValues(vals);
    }
    
    /**
     * create a matrix dropping a specific column
     * @param colToDrop index of column to Drop
     * @return the modified matrix
     */
    private Matrix dropCol(int colToDrop){
        colToDrop--;
        double[] vals = new double[NUM_ROWS*(NUM_COLS-1)];
        int i=0;
        for(int r=0; r<NUM_ROWS; r++)
            for(int c=0; c<NUM_COLS; c++)
                if(c!=colToDrop){
                    vals[i] = matrix[r][c];
                    i++;
                }
        return (new Matrix(NUM_ROWS, NUM_COLS-1)).setValues(vals);
    }
    
    /**
     * @return the determinant of the matrix (if square) 
     */
    public double getDeterminant(){
        if(!isSquare){
            System.out.println("CANT CALCULATE THE DETERMINANT OF A MATRIX THAT IS NOT SQUARE");
            return 1;
        }
        switch(NUM_ROWS){
            case 1:
                return matrix[0][0];
            case 2:
                return (matrix[0][0]*matrix[1][1])-(matrix[0][1]*matrix[1][0]);
            case 3:
                return detWithSarrus();
            default: 
        }
        if(NUM_ROWS>3) return detWithLaplace();
        else return 1;
    }
    
    /**
     * @return the determinant with sarrus
     */
    private double detWithSarrus(){
        double det = 
                matrix[0][0]*matrix[1][1]*matrix[2][2] +
                matrix[0][1]*matrix[1][2]*matrix[2][0] +
                matrix[0][2]*matrix[1][0]*matrix[2][1] -
                matrix[2][0]*matrix[1][1]*matrix[0][2] -
                matrix[2][1]*matrix[1][2]*matrix[0][0] -
                matrix[2][2]*matrix[1][0]*matrix[0][1];
        return det;
    }
    
    /**
     * @return the determinant with laplace
     */
    private double detWithLaplace(){
        int det = 0;
        for(int i=0, j=0; i<NUM_ROWS; i++)
            det += pow(-1, i+j)*matrix[i][j]*(this.dropRow(i+1).dropCol(j+1).getDeterminant());       
        return det;
    }
    
    public int getRang(){
    	return 0;
    }
    
    @Override
    public String toString() {
        String s = "Matrice " + NUM_ROWS + "x" + NUM_COLS + ":\n";
        for(double[] r: matrix){
            for(double c: r)
                s += "\t" + c;
            s += "\n";
        }
        if(isSquare)
            s += "determinant: " + getDeterminant() +"\n";
        return s;
    }   
    
    public static void main(String[] args) {
		// TODO Auto-generated method stub
    	System.out.println("culo");
	}
}
