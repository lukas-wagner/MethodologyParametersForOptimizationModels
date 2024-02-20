package systemParameterExtraction;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.rcaller.exception.ExecutionException;
import com.github.rcaller.rstuff.RCaller;
import com.github.rcaller.rstuff.RCode;

import designpatterns.PiecewiseLinearApproximation;



public class Regression {

	public static void main(String[] args) {
		/**
		 * Creating vectors x and y
		 * exemplary values
		 */
		double[] soc = new double[]{0, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1100, 1200, 1300, 1400, 1500, 1600, 1700, 1800, 1900, 2000, 2100, 2200, 2300, 2400, 2500, 2600, 2700, 2800, 2900, 3000, 3100, 3200, 3300, 3400, 3500, 3600, 3700, 3800, 3900, 4000, 4100, 4200, 4300, 4400, 4500, 4600, 4700, 4800, 4900, 5000, 5100, 5200, 5300, 5400, 5500, 5600, 5700, 5800, 5900, 6000};
		double[] deg = new double[]{0.000, 0.458, 0.591, 0.634, 0.654, 0.665, 0.672, 0.676, 0.679, 0.680, 0.681, 0.681, 0.681, 0.680, 0.680, 0.679, 0.678, 0.676, 0.675, 0.673, 0.672, 0.670, 0.668, 0.667, 0.665, 0.663, 0.661, 0.659, 0.657, 0.655, 0.653, 0.651, 0.649, 0.647, 0.645, 0.643, 0.641, 0.639, 0.637, 0.635, 0.633, 0.631, 0.629, 0.627, 0.624, 0.622, 0.620, 0.618, 0.616, 0.614, 0.611, 0.609, 0.607, 0.605, 0.603, 0.601, 0.598, 0.596, 0.594, 0.592, 0.590};
		double[] dasdf = new double[]{0.000, 0.458, 0.591, 0.634, 0.654, 0.665, 0.672, 0.676, 0.679, 0.680, 0.681, 0.681, 0.681, 0.680, 0.680, 0.679, 0.678, 0.676, 0.675, 0.673, 0.672, 0.670, 0.668, 0.667, 0.665, 0.663, 0.661, 0.659, 0.657, 0.655, 0.653, 0.651, 0.649, 0.647, 0.645, 0.643, 0.641, 0.639, 0.637, 0.635, 0.633, 0.631, 0.629, 0.627, 0.624, 0.622, 0.620, 0.618, 0.616, 0.614, 0.611, 0.609, 0.607, 0.605, 0.603, 0.601, 0.598, 0.596, 0.594, 0.592, 0.590};

		//		linearRegression(soc, deg);
		multipleLinearRegression(new double[][] {soc, dasdf}, deg);
		//		calcPla(soc, deg);

	}

	//	public Regression (double[] x, double[] y) {
	//		calcPla(x, y);
	//	}

	/**
	 * Linear regression.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the object[]: [0] PiecewiseLinearApproximation, [1]: rsquared: [][0] r^2, [][1] adjR^2
	 */
	public static Object[] linearRegression (double[] x, double[] y) {
		PiecewiseLinearApproximation regressionParameters = new PiecewiseLinearApproximation();
		Object[] regParametersAndR2 = new Object[2];
		try {
			RCaller caller = RCaller.create();
			RCode code = RCode.create();

			code.addDoubleArray("x", x);
			code.addDoubleArray("y", y);

			/**
			 * R code for regression of y on x
			 */
			code.addRCode("linreg<-lm(y~x)");
			code.addRCode("myResult <- list(rsq =summary(linreg)$r.squared, adjrsq= summary(linreg)$adj.r.squared, coefficients = coef(linreg))");
			caller.setRCode(code);
			caller.runAndReturnResult("myResult");

			double[] rsquared = caller.getParser().getAsDoubleArray("rsq");
			double[] adjrsquared = caller.getParser().getAsDoubleArray("adjrsq");
			double[] coefficients = caller.getParser().getAsDoubleArray("coefficients");

			double[] rsquaredAndAjd = new double[] {rsquared[0], adjrsquared[0]};
			//			System.out.println("R2: "  + rsquared[0] + " adjR2: " + adjrsquared[0]);
			regressionParameters.setSlope(coefficients[1]);
			regressionParameters.setIntercept(coefficients[0]);
			regParametersAndR2 = new Object[] {regressionParameters, rsquaredAndAjd};

		} catch (Exception e) {
			/**
			 * Note that, RCaller does some OS based works such as creating an external process and
			 * reading files from temporary directories or creating images for plots. Those operations
			 * may cause exceptions for those that user must handle the potential errors. 
			 */
			Logger.getLogger(Regression.class.getName()).log(Level.SEVERE, e.getMessage());
		}


		return regParametersAndR2;
	}

	/**
	 * Use R caller to determine piecewise linear approx
	 */
	public static Object[] calcPla (double[] x, double[] y, double quantileDistance) throws ExecutionException {
		List<PiecewiseLinearApproximation> pla = new ArrayList<PiecewiseLinearApproximation>();
		Object[] plaParametersAndR2 = new Object[2];
		//		try {
		RCaller caller = RCaller.create();
		RCode code = RCode.create();

		/**
		 * Converting Java arrays to R arrays
		 */
		code.addDoubleArray("x", x);
		code.addDoubleArray("y", y);

		/**
		 * R code for regression of deg on soc
		 */
		code.addRCode("linreg<-lm(y~x)");

		//# Extract the coefficients from the overall model
		code.addRCode("my.coef <- coef(linreg)");
		// Calculate quantiles of the predictor variable (y)
		code.addRCode("quantilesy <- quantile(y, probs = seq(0, 1, by ="+ Double.toString(quantileDistance)+"))");

		code.addRCode("library(segmented)");
		// https://search.r-project.org/CRAN/refmans/segmented/html/seg.control.html
		//code.addRCode("my.seg <-segmented(my.lm,  seg.Z = ~ soc, psi = NA)");
		//code.addRCode("my.seg <-segmented(my.lm,  seg.Z = ~ soc, psi = list(soc = c(400,800)))"); 
		//			code.addRCode("segReg <-segmented(linreg,  seg.Z = ~ x,  npsi=1)"); 
		code.addRCode("segReg <-segmented(linreg,  seg.Z = ~ x,  psi = quantilesy)"); 

		code.addRCode("slope(segReg)");
		code.addRCode("combinedinformation <- list(rsq=summary(segReg)$r.squared, adjrsq= summary(segReg)$adj.r.squared, slope = slope(segReg), coefficients = coef(segReg), psi = segReg$psi)");
		caller.setRCode(code);
		caller.runAndReturnResult("combinedinformation");


		double[] coefficients = caller.getParser().getAsDoubleArray("coefficients");
		double[] breakpoints = caller.getParser().getAsDoubleArray("psi");
		double[] rsquared = caller.getParser().getAsDoubleArray("rsq");
		double[] adjrsquared = caller.getParser().getAsDoubleArray("adjrsq");
		//System.out.println("seg R2: "  + rsquared[0] + " adjR2: " + adjrsquared[0]);
		double[] rsquaredAndAjd = new double[] {rsquared[0], adjrsquared[0]};



		//  ------- Calculate slope and intercept -------
		//https://rpubs.com/MarkusLoew/12164

		int npsi = breakpoints.length/3;
		//System.out.println("number of breakpoints" + npsi);

		double intercept1 = coefficients[0];
		double slope1 = coefficients[1];
		PiecewiseLinearApproximation segment0 = new  PiecewiseLinearApproximation();
		// set ub at 0 for first segment
		segment0.setPla(intercept1, slope1, 0, breakpoints[npsi]);
		pla.add(segment0);

		for (int i = 1; i < npsi+1; i++) {
			//		 breakpoint[1] for npsi = 1, breakpoints[2] und [3] for npsi = 2,	npsi = 3 3,4,5
			int startingBreakpoint = i+npsi-1;

			//At the breakpoint (break1), the segments b and c intersect, #b0 + b1*x = c0 + c1*x
			// the coefficients are the differences in slope in comparison to the previous slope
			// slope = coefficient(i) + coefficient(i-1)
			double slope = coefficients[startingBreakpoint] + pla.get(i-1).getSlope();
			//intercept = intercept(-1) + slope(-1)*breakpoint - slope *breakpoint
			double intercept = 
					pla.get(i-1).getIntercept()
					+ pla.get(i-1).getSlope()*breakpoints[startingBreakpoint] 
							- slope * breakpoints[startingBreakpoint]; 

			PiecewiseLinearApproximation segmentI = new PiecewiseLinearApproximation();
			if (i == npsi) {
				// set ub of unlimited for last segment
				segmentI.setPla(intercept, slope, breakpoints[startingBreakpoint], Double.MAX_VALUE);
			} else {
				segmentI.setPla(intercept, slope, breakpoints[startingBreakpoint], breakpoints[startingBreakpoint+1]);
			}
			pla.add(segmentI);
		}

		//			for (int i = 0; i < pla.size(); i++) {
		//				System.out.println(i + " slope" + pla.get(i).getSlope() + " intercept " + pla.get(i).getIntercept() + "lb " + pla.get(i).getLowerBound() + " ub " + pla.get(i).getUpperBound());
		//			}
		//			System.out.println();
		code.clear();
		//			code.addRCode("results <- summary(my.seg)");

		// combine pla results and r^2 values in object[]
		plaParametersAndR2 = new Object[] {pla, rsquaredAndAjd};
		//		} catch (Exception e) {
		//			/**
		//			 * Note that, RCaller does some OS based works such as creating an external process and
		//			 * reading files from temporary directories or creating images for plots. Those operations
		//			 * may cause exceptions for those that user must handle the potential errors. 
		//			 */
		//			Logger.getLogger(Regression.class.getName()).log(Level.SEVERE, e.getMessage());
		//		}
		return plaParametersAndR2;	
	}

	//	public Regression (double[] x, double[] y) {
	//		calcPla(x, y);
	//	}

	/**
	 * Multiple linear regression.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the object[]: [0] PiecewiseLinearApproximation, [1]: rsquared: [][0] r^2, [][1] adjR^2
	 */
	public static Object[] multipleLinearRegression (double[][] x,  double[] y) {
		ArrayList<List<PiecewiseLinearApproximation>> regressionParameters = new ArrayList<List<PiecewiseLinearApproximation>>();
		Object[] regParametersAndR2 = new Object[2];
		try {
			RCaller caller = RCaller.create();
			RCode code = RCode.create();
			String xValueNames = ""; 

			//			for (int i = 0; i < x[0].length; i++) {
			//				System.out.println("1: " + x[0][i] +" 2:"+  x[1][i]);
			//			}

			for (int i = 0; i < x.length; i++) {
				System.out.println(i + " " + xValueNames);
				try {
					code.addDoubleArray("x"+Integer.toString(i), x[i]);
					//					code.addRCode("x"+Integer.toString(i)+"<- as.numeric(x"+Integer.toString(i)+")");
					if (i==0) {
						xValueNames = "x"+ Integer.toString(i);
					} else {
						xValueNames = xValueNames +"+x"+ Integer.toString(i);
					}
				} catch (Exception e) {
					System.err.println("Exception "+ e);
				}
			}
			System.out.println(xValueNames);
			code.addDoubleArray("y", y);

			code.addRCode("linreg<-lm(y~"+xValueNames+", na.action = na.omit)");
			code.addRCode("myResult <- list(rsq =summary(linreg)$r.squared, adjrsq= summary(linreg)$adj.r.squared, coefficients = coef(linreg), pvalues = summary(linreg)$coefficients[, \"Pr(>|t|)\"])");
			caller.setRCode(code);
			caller.runAndReturnResult("myResult");

			double[] rsquared = caller.getParser().getAsDoubleArray("rsq");
			double[] adjrsquared = caller.getParser().getAsDoubleArray("adjrsq");
			double[] coefficients = caller.getParser().getAsDoubleArray("coefficients");
			double[] pvalues = 	caller.getParser().getAsDoubleArray("pvalues");
			for (int i = 0; i < coefficients.length; i++) {
				System.out.println("coefficent" + i  + " " + coefficients[i]);
			}
			for (int j = 0; j < pvalues.length; j++) {
				System.out.println("pvalue " + j + " " + pvalues[j]);
			}

			double[] rsquaredAndAjd = new double[] {rsquared[0], adjrsquared[0]};
			System.out.println("R2: "  + rsquared[0] + " adjR2: " + adjrsquared[0]);

			List<PiecewiseLinearApproximation> input1 = new ArrayList<PiecewiseLinearApproximation>();
			PiecewiseLinearApproximation yx1 = new PiecewiseLinearApproximation();


			yx1.setIntercept(coefficients[0]);
			yx1.setSlope(coefficients[1]);
			input1.add(yx1);
			regressionParameters.add(input1);

			for (int inputs = 1; inputs < x.length; inputs++) {
				List<PiecewiseLinearApproximation> input2 = new ArrayList<PiecewiseLinearApproximation>();
				PiecewiseLinearApproximation yx2 = new PiecewiseLinearApproximation();
				yx2.setIntercept(0);
				yx2.setSlope(coefficients[inputs+1]);
				input2.add(yx2);
				regressionParameters.add(input2);
			}
			// always 0 as already in first element

			regParametersAndR2 = new Object[] {regressionParameters, rsquaredAndAjd};

		} catch (Exception e) {
			/**
			 * Note that, RCaller does some OS based works such as creating an external process and
			 * reading files from temporary directories or creating images for plots. Those operations
			 * may cause exceptions for those that user must handle the potential errors. 
			 */
			Logger.getLogger(Regression.class.getName()).log(Level.SEVERE, e.getMessage());
		}


		return regParametersAndR2;
	}

	//	public Regression (double[] x, double[] y) {
	//		calcPla(x, y);
	//	}

	/**
	 * Multiple linear regression.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the object[]: [0] PiecewiseLinearApproximation, [1]: rsquared: [][0] r^2, [][1] adjR^2
	 */
	public static Object[] multiplePiecewiseLinearRegression (double[][] x,  double[] y, double quantileDistance) throws ExecutionException {
		ArrayList<List<PiecewiseLinearApproximation>> regressionParameters = new ArrayList<List<PiecewiseLinearApproximation>>();
		Object[] regParametersAndR2 = new Object[2];
		try {
			RCaller caller = RCaller.create();
			RCode code = RCode.create();
			String xValueNames = ""; 

			//			for (int i = 0; i < x[0].length; i++) {
			//				System.out.println("1: " + x[0][i] +" 2:"+  x[1][i]);
			//			}

			for (int i = 0; i < x.length; i++) {
				System.out.println(i + " " + xValueNames);
				try {
					code.addDoubleArray("x"+Integer.toString(i), x[i]);
					//					code.addRCode("x"+Integer.toString(i)+"<- as.numeric(x"+Integer.toString(i)+")");
					if (i==0) {
						xValueNames = "x"+ Integer.toString(i);
					} else {
						xValueNames = xValueNames +"+x"+ Integer.toString(i);
					}
				} catch (Exception e) {
					System.err.println("Exception "+ e);
				}
			}
			System.out.println(xValueNames);
			code.addDoubleArray("y", y);

			code.addRCode("linreg<-lm(y~"+xValueNames+", na.action = na.omit)");

			code.addRCode("library(segmented)");
			code.addRCode("quantilesy <- quantile(y, probs = seq(0, 1, by ="+ Double.toString(quantileDistance)+"))");

			//			code.addRCode("segReg <-segmented(linreg,  seg.Z = ~ x,  psi = quantilesy)"); 			
			code.addRCode("segReg <-segmented(linreg,  seg.Z = ~"+xValueNames+")");

			String slopeVariable = "slopex0= slope(segReg)$x0"; 
			String interceptVariable = "interceptx0 = intercept(segReg)$x0"; 
			for (int i = 1; i < x.length; i++) {
				slopeVariable = slopeVariable + ", slopex"+Integer.toString(i)+"= slope(segReg)$x"+Integer.toString(i);
				interceptVariable = interceptVariable + ", interceptx"+Integer.toString(i)+"= intercept(segReg)$x"+Integer.toString(i);
			}
			
			code.addRCode("myResultSegReg <- list(rsq=summary(segReg)$r.squared, adjrsq= summary(segReg)$adj.r.squared, coefficients = coef(segReg), psi = segReg$psi,"+slopeVariable+ "," + interceptVariable+")");//, pvalues = summary(linreg)$coefficients[, \"Pr(>|t|)\"]");

			caller.setRCode(code);
			caller.runAndReturnResult("myResultSegReg");


			double[] coefficients = caller.getParser().getAsDoubleArray("coefficients");
			double[] breakpoints = caller.getParser().getAsDoubleArray("psi");
//			double[] pvalues = caller.getParser().getAsDoubleArray("pvalues");

			double[] slopex0 = caller.getParser().getAsDoubleArray("slopex0");
			double[] interceptx0 = caller.getParser().getAsDoubleArray("interceptx0");

			double[] slopex1 = caller.getParser().getAsDoubleArray("slopex1");
			double[] interceptx1 = caller.getParser().getAsDoubleArray("interceptx1");
			
			double[] rsquared = caller.getParser().getAsDoubleArray("rsq");
			double[] adjrsquared = caller.getParser().getAsDoubleArray("adjrsq");
			System.out.println("seg R2: "  + rsquared[0] + " adjR2: " + adjrsquared[0]);
			double[] rsquaredAndAjd = new double[] {rsquared[0], adjrsquared[0]};

			for (int i = 0; i < breakpoints.length; i++) {
				System.out.println("breakpoints " + i + " " + breakpoints[i]);
			}

			for (int i = 0; i < coefficients.length; i++) {
				System.out.println("coefficients " + i + " " + coefficients[i]);
			}

			for (int i = 0; i < slopex0.length; i++) {
				System.out.println("slopex0 " + i + " " + slopex0[i]);
			}
			
			for (int i = 0; i < interceptx0.length; i++) {
				System.out.println("interceptx0 " + i + " " + interceptx0[i]);
			}
			
			for (int i = 0; i < slopex1.length; i++) {
				System.out.println("slopex1 " + i + " " + slopex1[i]);
			}
			
			for (int i = 0; i < interceptx1.length; i++) {
				System.out.println("interceptx1 " + i + " " + interceptx1[i]);
			}
			
			for (int inputs = 0; inputs < x.length; inputs++) {
				List<PiecewiseLinearApproximation> inputn = new ArrayList<PiecewiseLinearApproximation>();

				for (int counter = 0; counter < breakpoints.length; counter++) {
					// TODO breakpoints.length is the wrong variable
					PiecewiseLinearApproximation yxn = new PiecewiseLinearApproximation();
					//TODO calc slope
					yxn.setSlope(slopex0[counter]);

					//TODO calc setIntercept
					yxn.setIntercept(interceptx0[counter]);

					// TODO
					yxn.setLowerBound(-1);
					yxn.setUpperBound(-1);
					inputn.add(yxn);
				}
				regressionParameters.add(inputn);
			}

			regParametersAndR2 =  new Object[] {regressionParameters, rsquaredAndAjd};
		} catch (Exception e) {
			/**
			 * Note that, RCaller does some OS based works such as creating an external process and
			 * reading files from temporary directories or creating images for plots. Those operations
			 * may cause exceptions for those that user must handle the potential errors. 
			 */
			Logger.getLogger(Regression.class.getName()).log(Level.SEVERE, e.getMessage());
		}


		return regParametersAndR2;
	}
}
