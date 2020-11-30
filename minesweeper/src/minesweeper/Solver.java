package minesweeper;
import java.util.*;
import java.util.concurrent.TimeUnit;
public class Solver {
	
//	TRUONG Khac Minh Tam 19009390
//  AI utilise matrice et elimination de Gaussian pour chercher les cases secures et des mines.	
//	Pour la question 5c, avec 100000 de simulation: le taux de reussite est 82.5% pour la grille 8x8-10; 77.2% pour la grille 16x16-40 et 20,75% pour la grille 16x30-99.
	
	
	//affiche
	public static void afficheTab(int[] tab) {
        for(int i=0; i<tab.length;i++) {
            System.out.print(tab[i]+" ");
        } System.out.println();
    }
	public static void afficheTab2d(int[][] t) {
		for(int i=0;i<t.length;++i) {
			for(int j=0;j<t[i].length;++j) {
				if(t[i][j]<0) System.out.print("   "+t[i][j]+"   ");
				else if(t[i][j]<=9) System.out.print("   "+t[i][j]+"  ");
				else if(t[i][j]<=999) System.out.print(" "+t[i][j]+" ");
				else System.out.print(t[i][j]+"  ");
			}System.out.println();
		}
	}
	
	//jeu
	public static int[][] T;
	public static int[][] Tadj;
	public static void init(int h,int l,int n) {
		T=new int[h][l];
		Tadj= new int[h][l];
		if (n>h*l) n=h*l;
		while(n>0) {
			int i=(int)(Math.random()*h);
			int j=(int)(Math.random()*l);
			if(Tadj[i][j]!=-1) {
				Tadj[i][j]=-1;
				--n;
			}
		}
	}
	
	public static int[] casesAutour(int i, int j) {
		int cpt=0;
		for(int a=i-1;a<=i+1;++a) {
			for(int b=j-1;b<=j+1;++b) {
				if(!(a==i & b==j) && caseCorrect(a,b)) cpt++; 
			}
		}
		int[] ca=new int[cpt];;
		cpt=0;
		for(int a=i-1;a<=i+1;++a) {
			for(int b=j-1;b<=j+1;++b) {
				if(!(a==i & b==j) && caseCorrect(a,b)) 
				ca[cpt++]=a*100+b;
			}
		}
		return ca;
	}
	
	public static boolean caseCorrect(int i,int j) {
		return (i>=0 && j>=0 && i<T.length && j<T[0].length);
	}
	public static void calculerAdjacent() {
		for(int i=0;i<T.length;++i) {
			for(int j=0;j<T[0].length;++j) {
				if(Tadj[i][j]!=-1) {
					int cpt=0;
					int[] ca=casesAutour(i,j);
					for(int m=0;m<ca.length;++m) {
						if(Tadj[(ca[m]-ca[m]%100)/100][ca[m]%100]==-1) cpt++;
					}Tadj[i][j]=cpt;
				}
			}
		}
	}
	
	public static void afficherGrille(boolean affMines) {
		String[][] g=new String[T.length][T[0].length];
		if(affMines==true) 
			for(int a=0;a<T.length;++a) {
			for(int b=0;b<T[0].length;++b) {
				if(Tadj[a][b]==-1) {
					g[a][b]="!";
					T[a][b]=1;
				}
			}
		}
		for(int i=0;i<g.length;++i) {
			for(int j=0;j<g[0].length;++j) {
				if(T[i][j]==0) g[i][j]=" ";
				else if(T[i][j]==2) g[i][j]="X";
				else if(Tadj[i][j]!=-1) g[i][j]=Integer.toString(Tadj[i][j]);
			}
		}
		System.out.print("  |");
		for(int i=0;i<T[0].length;++i) {
			if(i<26) System.out.print((char)(i+65)+"|");
			else System.out.print((char)(i+71)+"|");
		}System.out.println();
		for(int i=0;i<g.length;++i) {
			if(i<10) System.out.print(" "+i+"|");
			else System.out.print(i+"|");
			for(int j=0;j<g[i].length;++j) {
				if(T[i][j]==0) g[i][j]=" ";
				System.out.print(g[i][j]+"|");
			}System.out.println();
		}
	}

	public static boolean caseAdjacenteZero(int i, int j) {
		int[] ca=casesAutour(i,j);
		boolean res=false;
		int m=0;
		while(!res && m<ca.length) {
			if(T[(ca[m]-ca[m]%100)/100][ca[m]%100]==1 
					&& Tadj[(ca[m]-ca[m]%100)/100][ca[m]%100]==0)
				res=true;
			m++;
		}
		return res;
	}
	public static void revelation(int i, int j) {
		T[i][j]=1;
		int[] ca=casesAutour(i,j);
		for(int m=0;m<ca.length;++m) {
			if(T[(ca[m]-ca[m]%100)/100][ca[m]%100]==0
					&& caseAdjacenteZero((ca[m]-ca[m]%100)/100,ca[m]%100))
				revelation((ca[m]-ca[m]%100)/100,ca[m]%100);
		}
	}
	public static boolean revelerCase(int i, int j, boolean drapeau) {
		if(drapeau) {
			if(T[i][j]==0) T[i][j]=2;
			else if(T[i][j]==2) T[i][j]=0;
			return true;
		} 
		if(Tadj[i][j]==-1) return false;
		revelation(i,j);
		return true;
	}
	
	public static boolean aGagne() {
		int cpt1=0;
		int cpt2=0;
		for(int i=0;i<T.length;++i) {
			for(int j=0;j<T[0].length;++j) {
				if(T[i][j]==1) cpt1++;
				if(Tadj[i][j]==-1) cpt2++;
			}
		}return (cpt1==(T.length*T[0].length)-cpt2);
	}
	
	private static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    return true;
	}
	public static boolean verifierFormat(String s) {
		if(s.compareTo("AI")==0) return true;
		if(s.compareTo("matrix")==0) return true;
		if(s.compareTo("bdr")==0) return true;
		if(s.compareTo("50544E")==0) return true;
		if(s.compareTo("aide")==0) return true;
		if(s.compareTo("rand")==0) return true;
		if(s.compareTo("cano")==0) return true;
		if(s.length()>=3) {
		if(s.charAt(0)=='d' || s.charAt(0)=='r' ) {
			if(isInteger(s.substring(1, s.length()-1)) 
					&& Integer.parseInt(s.substring(1, s.length()-1))>=0 
					&& Integer.parseInt(s.substring(1, s.length()-1))<T.length) {
				if(T[0].length<27) { 
					if(s.charAt(s.length()-1)>=(char)65 
							&& s.charAt(s.length()-1)<(char)T[0].length+65) return true;
				}else if(s.charAt(s.length()-1)>=(char)65 
							&& s.charAt(s.length()-1)<(char)T[0].length+71
							&& s.charAt(s.length()-1)!=(char)91
							&& s.charAt(s.length()-1)!=(char)92
							&& s.charAt(s.length()-1)!=(char)93
							&& s.charAt(s.length()-1)!=(char)94
							&& s.charAt(s.length()-1)!=(char)95
							&& s.charAt(s.length()-1)!=(char)96
							)return true;
				}
			}
		}
		return false;
	}
	
	public static int[] conversionCoordonnees(String input) {
		int[] t=new int[3];
		t[0]=Integer.parseInt(input.substring(1, input.length()-1));
		if(input.charAt(0)=='r') t[2]=1;
		else t[2]=0;
		if(input.charAt(input.length()-1)<='Z') t[1]=(int)(input.charAt(input.length()-1)-65);
		else t[1]=(int)(input.charAt(input.length()-1)-71);
		return t;
	}
	
	@SuppressWarnings("resource")
	public static void jeu() throws InterruptedException {
		Scanner sc=new Scanner(System.in);
		Boolean affMines=false;
		while(!aGagne() && !affMines) {
			String commande="";
			System.out.println();
			afficherGrille(affMines);
			System.out.println();
			boolean verFor=false;
			while(!verFor) {
			System.out.print("> Saisir une commande:");
			commande=sc.next();
			if(!verifierFormat(commande)) 
			System.out.println("Mauvais format!");
			else verFor=true;
			}
			if(Objects.equals(commande,"matrix")) {
				int[][] matrix=initMatrix();
				System.out.println();
				afficheTab2d(matrix);
				System.out.println();
				solveMatrix(matrix);
				afficheTab2d(matrix);
				readMatrixAI(matrix);
			}
			else if(Objects.equals(commande,"50544E")) {afficheTab2d(Tadj); System.out.println("Tricheur!!!");}
			else if(Objects.equals(commande,"AI")) {
				affMines=!revelerCase(0,0,false);
				boolean c=false;
				while(!c && !aGagne() && !affMines) {
					System.out.println();
					int[][] tdebut=new int[T.length][T[0].length];
						for(int i=0;i<T.length;++i) {
							for(int j=0;j<T[0].length;++j) {
								tdebut[i][j]=T[i][j];
							}
						}
					drapeauAuto();
					casesSansMineAI();
					int[][] matrix=initMatrix();
					solveMatrix(matrix);
					readMatrixAI(matrix);
					TimeUnit.MILLISECONDS.sleep(400);
					afficherGrille(affMines);
					int[][] tfin=new int[T.length][T[0].length];
					for(int i=0;i<T.length;++i) {
						for(int j=0;j<T[0].length;++j) {
							tfin[i][j]=T[i][j];
						}
					}
					boolean diff=false;
					int m=0;
					while(!diff && m<tdebut.length) {
						if(!Arrays.equals(tdebut[m],tfin[m])) diff=true; 
						m++;
					}
					if(diff==false) {
						System.out.println();
						System.out.println("Aucune cases possible, revele aleatoirement?");
						commande=sc.next();
						if(commande.equals("1")) affMines=!randomReveal();
						else if(commande.equals("0")) c=true;
						else System.out.println("Veuillez saisir 1 pour Oui 0 pour Non");
					}
				}
			} 
			else if(Objects.equals(commande,"rand")) affMines=!randomReveal();
			else if(Objects.equals(commande,"aide")) {
			drapeauAuto();
			System.out.println();
			casesSansMine();
			} 
			else {	
			int[] conversion=conversionCoordonnees(commande);
			affMines=!revelerCase(conversion[0],conversion[1],conversion[2]==0);
			}
			if(affMines) {
				System.out.println();
				afficherGrille(affMines);
				System.out.println();
				System.out.println("Vous avez perdu!");
			}
			if(aGagne()) {
				System.out.println();
				afficherGrille(affMines);
				System.out.println();
				System.out.println("Felicitation!");
				System.out.println("Vous avez gagne'!");
			}
		}
	}
	
	//aide
	public static void drapeauAuto() {
		for(int i=0;i<T.length;++i) {
			for(int j=0;j<T[0].length;++j) {
				if(T[i][j]==1 && Tadj[i][j]!=-1) {
					int[] ca=casesAutour(i,j);
					int cpt=0;
					for(int m=0;m<ca.length;++m) {
						if(T[(ca[m]-ca[m]%100)/100][ca[m]%100]==0 || T[(ca[m]-ca[m]%100)/100][ca[m]%100]==2 ) cpt++;
					}
					if(Tadj[i][j]==cpt) {
						for(int m=0;m<ca.length;++m) {
							if(T[(ca[m]-ca[m]%100)/100][ca[m]%100]==0) T[(ca[m]-ca[m]%100)/100][ca[m]%100]=2;
						}
					}
				}
			}
		}
	}
	public static void casesSansMine() {

		int[][] t=new int[T.length][T[0].length];
		for(int i=0;i<T.length;++i) {
			for(int j=0;j<T[0].length;++j) {
				if(T[i][j]==1 && Tadj[i][j]!=-1) {
					int[] ca=casesAutour(i,j);
					int cpt=0;
					for(int m=0;m<ca.length;++m) {
						if(T[(ca[m]-ca[m]%100)/100][ca[m]%100]==2) cpt++;
					}
					if(Tadj[i][j]==cpt) {
						for(int m=0;m<ca.length;++m) {
							if(T[(ca[m]-ca[m]%100)/100][ca[m]%100]==0 && t[(ca[m]-ca[m]%100)/100][ca[m]%100]!=-1) {
								if(ca[m]%100<26) {
									System.out.println("Case "+((ca[m]-ca[m]%100)/100)+","+(char)(ca[m]%100+65)+" est secure.");
									t[(ca[m]-ca[m]%100)/100][ca[m]%100]=-1;
								}
								else {
									System.out.println("Case "+((ca[m]-ca[m]%100)/100)+","+(char)(ca[m]%100+71)+" est secure.");
									t[(ca[m]-ca[m]%100)/100][ca[m]%100]=-1;
								}
							}
						}
					}
				}
			}
		}
	}
	public static void readMatrix(int[][] matrix) {
		boolean v;
		for(int i=matrix.length-1;i>=1;--i) {
			v=true;	
			for(int j=0;j<matrix[0].length-1;++j) {
					if(matrix[i][j]<0) v=false;
			}
			if(v==true) {
				
				if(matrix[i][matrix[0].length-1]==0) {
					for(int j=0;j<matrix[0].length-1;++j) 
						if(matrix[i][j]==1)
						System.out.println("Case "+((matrix[0][j]-matrix[0][j]%100)/100)+","+(char)(matrix[0][j]%100+65)+" est secure.");
					
				}
				else {
					int s=0;
				for(int j=0;j<matrix[0].length-1;++j) {
					s+=matrix[i][j];
				}
				if(matrix[i][matrix[0].length-1]==s) {
					for(int j=0;j<matrix[0].length-1;++j) 
						if(matrix[i][j]==s)
						System.out.println("Case "+((matrix[0][j]-matrix[0][j]%100)/100)+","+(char)(matrix[0][j]%100+65)+" est une mine.");
				}
				}
			}
		}
	}
	
	//MSsolver
	public static boolean randomReveal() {
		while(true) {
		int i=(int)(Math.random()*T.length);
		int j=(int)(Math.random()*T[0].length);
		
		if(T[i][j]==0) 
			return revelerCase(i,j,false);
		}
	}
	public static int[] casesAdjNonOuvert() {
		int cpt=0;
		for(int i=0;i<T.length;++i) {
			for(int j=0;j<T[0].length;++j) {
				int[] ca=casesAutour(i,j);
				boolean res=false;
				int m=0;
				while(!res && m<ca.length) {
					if(T[i][j]==1 
							&& T[(ca[m]-ca[m]%100)/100][ca[m]%100]==0) {
						res=true;
						cpt++;
					}
					m++;
				}
			}
		}
		int[] cano=new int[cpt];
		cpt=0;
		for(int i=0;i<T.length;++i) {
			for(int j=0;j<T[0].length;++j) {
				int[] ca=casesAutour(i,j);
				boolean res=false;
				int m=0;
				while(!res && m<ca.length) {
					if(T[i][j]==1 
							&& T[(ca[m]-ca[m]%100)/100][ca[m]%100]==0 ) {
						res=true;
						cano[cpt++]=i*100+j;
					}
					m++;
				}
			}
		}
		return cano;
	}
	public static int[] border() {
		int[] cano=casesAdjNonOuvert();
		int[][] t=new int[T.length][T[0].length];
		int cpt=0;
		for(int n=0;n<cano.length;++n) {
			int[] ca=casesAutour((cano[n]-cano[n]%100)/100,cano[n]%100);
			for(int m=0;m<ca.length;++m) {
				if(T[(ca[m]-ca[m]%100)/100][ca[m]%100]==0
						&& t[(ca[m]-ca[m]%100)/100][ca[m]%100]!=-1) {
					t[(ca[m]-ca[m]%100)/100][ca[m]%100]=-1;
					cpt++;
				}
			}
		}
		int[] bdr=new int[cpt];
		cpt=0;
		for(int n=0;n<cano.length;++n) {
			int[] ca=casesAutour((cano[n]-cano[n]%100)/100,cano[n]%100);
			for(int m=0;m<ca.length;++m) {
				if(T[(ca[m]-ca[m]%100)/100][ca[m]%100]==0
						&& t[(ca[m]-ca[m]%100)/100][ca[m]%100]!=1) {
					t[(ca[m]-ca[m]%100)/100][ca[m]%100]=1;
					bdr[cpt++]=ca[m];
				}
			}
		}
		return bdr;
	}
	public static boolean isNeighbor(int i, int j, int m, int n) {
		return Math.max(Math.abs(i - m), Math.abs(j - n)) <= 1;
	}
	public static int[][] initMatrix(){
		int[] bdr=border();
		int[] cano=casesAdjNonOuvert();
		int[][] matrix=new int[cano.length+1][bdr.length+1];
		
		for(int i=0; i<bdr.length; ++i) matrix[0][i]=bdr[i];
		for(int i=0; i<cano.length;++i) {
			int cpt=0;
			int[] ca=casesAutour((cano[i]-cano[i]%100)/100,cano[i]%100);
			for(int m=0;m<ca.length;++m) if(T[(ca[m]-ca[m]%100)/100][ca[m]%100]==2) cpt++;
			matrix[i+1][bdr.length]=Tadj[(cano[i]-cano[i]%100)/100][cano[i]%100]-cpt;
		}
		
		for(int i=1; i<matrix.length;++i) {
			for(int j=0; j<matrix[0].length-1;++j) {
				if(T[(matrix[0][j]-matrix[0][j]%100)/100][matrix[0][j]%100]==0 
						&& isNeighbor((matrix[0][j]-matrix[0][j]%100)/100,matrix[0][j]%100,(cano[i-1]-cano[i-1]%100)/100,cano[i-1]%100))
					matrix[i][j]++;
			}
		}
		
		return matrix;
	}
	public static void solveMatrix(int[][] matrix) {
		int l=0;
		for(int i=1; i<matrix.length;++i) {
			// is pivot?
			boolean v=false;
			while(matrix[i][l]==0 && l<matrix[0].length-1 && !v  && i!=matrix.length-1) {
				for(int j=i+1;j<matrix.length;++j) {
					if(matrix[j][l]!=0) v=true;
				}
				if(v==false && l<matrix[0].length-1) l++;
			}
			// row swap if !=1
			if(matrix[i][l]!=1) {
				for(int j=i+1;j<matrix.length;++j) {
					if(matrix[j][l]!=0) {
						int[] tmp=matrix[j];
						matrix[j]=matrix[i];
						matrix[i]=tmp;
					}
				}
			}
			//mutiply by -1
			if(matrix[i][l]==-1) {
				for(int j=0;j<matrix[0].length;++j) matrix[i][j]=matrix[i][j]*(-1);
			}
			// eliminate column
			for(int j=1;j<matrix.length;++j) {
				if(j!=i && matrix[j][l]!=0) {
					int c=matrix[i][l]/matrix[j][l];
					for(int m=0;m<matrix[0].length;++m) {
						matrix[j][m]=matrix[j][m]-(c*matrix[i][m]);
					}
				}
			}
			if(l<matrix[0].length-1) l++;
		}
	}
	public static void readMatrixAI(int[][] matrix) {
		boolean v;
		for(int i=matrix.length-1;i>=1;--i) {
			v=true;	
			for(int j=0;j<matrix[0].length-1;++j) {
					if(matrix[i][j]<0) v=false;
			}
			if(v==true) {
				
				if(matrix[i][matrix[0].length-1]==0) {
					for(int j=0;j<matrix[0].length-1;++j) 
						if(matrix[i][j]==1)
						revelerCase((matrix[0][j]-matrix[0][j]%100)/100,(matrix[0][j]%100),false);
					
				}
				else {
					int s=0;
				for(int j=0;j<matrix[0].length-1;++j) {
					s+=matrix[i][j];
				}
				if(matrix[i][matrix[0].length-1]==s) {
					for(int j=0;j<matrix[0].length-1;++j) 
						if(matrix[i][j]==1)
							revelerCase((matrix[0][j]-matrix[0][j]%100)/100,(matrix[0][j]%100),true);
				}
				}
			}
		}
	}
	public static void casesSansMineAI() {

	int[][] t=new int[T.length][T[0].length];
		for(int i=0;i<T.length;++i) {
			for(int j=0;j<T[0].length;++j) {
				if(T[i][j]==1 && Tadj[i][j]!=-1) {
					int[] ca=casesAutour(i,j);
					int cpt=0;
					for(int m=0;m<ca.length;++m) {
						if(T[(ca[m]-ca[m]%100)/100][ca[m]%100]==2) cpt++;
					}
					if(Tadj[i][j]==cpt) {
						for(int m=0;m<ca.length;++m) {
							if(T[(ca[m]-ca[m]%100)/100][ca[m]%100]==0 && t[(ca[m]-ca[m]%100)/100][ca[m]%100]!=-1) {
								if(ca[m]%100<26) {
									t[(ca[m]-ca[m]%100)/100][ca[m]%100]=-1;
									revelerCase((ca[m]-ca[m]%100)/100,ca[m]%100,false);
								}
								else {								
									t[(ca[m]-ca[m]%100)/100][ca[m]%100]=-1;
									revelerCase((ca[m]-ca[m]%100)/100,ca[m]%100,false);
								}
							}
						}
					}
				}
			}
		}
	}
	
	//Main
	public static void main(String[] args) throws InterruptedException {
			Scanner sc=new Scanner(System.in);
			
			System.out.println("                             DEMINEUR");
			System.out.println();
			System.out.println("1-Commencer     2-Simulateur IA     3-Instruction     4-Quitter");
			System.out.println();
			//Lancer le jeu
			boolean ingame=true;
			while(ingame) {
			System.out.println("> Saisir une commande: ");
			String commande=sc.next();
			//1-Commencer
			if(commande.equals("1")) {
			boolean rc=false;
			while(!rc) {
			boolean vd=false;
			int h=1,l=1,n=0;
			while(!vd) {
			System.out.println();
			System.out.println("CONFIGURATION:");
				boolean error=true;
				do {
					try {
			System.out.println("-Entrer hauteur: ");
			h=sc.nextInt();
			System.out.println("-Entrer longeur: ");
			l=sc.nextInt();
			System.out.println("-Entrer nombre de mines: ");
			n=sc.nextInt();
			error=false;
					} catch(Exception e) {
						System.out.println("Veuillez entrer un nombre entier");
						System.out.println();
						sc.next();
					}
				} while(error);
			if(h<=0 || l<=0) System.out.println("Hauteur et Longeur ne doivent pas etre null.");
			else if(h>100 || l>52) System.out.println("Hauteur Max=100, Longeur Max=52.");
			else if(n==h*l) System.out.println("Toutes les cases sont des mines.");
			else vd=true;
			System.out.println();
			}
			init(h,l,n);
			calculerAdjacent();
			jeu();
			System.out.println();
			System.out.println("Partie Finie");
			System.out.println();
			while(!rc) {
				System.out.println("> Recommencer?");
				String rcc=sc.next();
				if(rcc.equals("1")) {rc=false; break;}
				else if(rcc.equals("0")) rc=true;
				else System.out.println("Veuillez saisir 1 pour Oui 0 pour Non");
				System.out.println();
			}
			}
			}
			//Simulation AI
			else if(commande.equals("2")) {
				boolean rc=false;
				while(!rc) {
				boolean vd=false;
				int h=1,l=1,n=0,f=1;
				while(!vd) {
				System.out.println();
				System.out.println("CONFIGURATION:");
					boolean error=true;
					do {
						try {
				System.out.println("-Entrer hauteur: ");
				h=sc.nextInt();
				System.out.println("-Entrer longeur: ");
				l=sc.nextInt();
				System.out.println("-Entrer nombre de mines: ");
				n=sc.nextInt();
				System.out.println("-Entrer nombre de simulation: ");
				f=sc.nextInt();
				error=false;
						} catch(Exception e) {
							System.out.println("Veuillez entrer un nombre entier");
							System.out.println();
							sc.next();
						}
					} while(error);
				if(h<=0 || l<=0 || f<=0) System.out.println("Hauteur, Longeur et Nombre de partie  ne doivent pas etre null.");
				else if(h>100 || l>52) System.out.println("Hauteur Max=100, Longeur Max=52.");
				else if(n==h*l) System.out.println("Toutes les cases sont des mines.");
				else vd=true;
				}
				System.out.print("Chargement");
				int vic=0,lost=0;
				double nbrd=0;
				for(int g=1;g<=f;++g) {
					if(f%10==0) {if((((double)g/f)*100)%2==0) System.out.print(".");}
					else { int cpt=1; if((((double)g/f)*100)%43<1 && cpt-->0)System.out.print(".");}
					//init
				T=new int[h][l];
				Tadj= new int[h][l];
				int tmp=n;
				while(n>0) {
					int i=(int)(Math.random()*h);
					int j=(int)(Math.random()*l);
					if(Tadj[i][j]!=-1) {
						if((h*l-n>9)) {
							if(!isNeighbor(i,j,h/2,l/2)) {
								Tadj[i][j]=-1;
								--n;
							}
						}
						else {
						Tadj[i][j]=-1;
						--n;
						}
					}
				}
				calculerAdjacent();
				boolean	affMines=!revelerCase(h/2,l/2,false);
				while(!aGagne() && !affMines) {
					int[][] tdebut=new int[T.length][T[0].length];
						for(int i=0;i<T.length;++i) {
							for(int j=0;j<T[0].length;++j) {
								tdebut[i][j]=T[i][j];
							}
						}
					drapeauAuto();
					casesSansMineAI();
					int[][] matrix=initMatrix();
					solveMatrix(matrix);
					readMatrixAI(matrix);
					int[][] tfin=new int[T.length][T[0].length];
					for(int i=0;i<T.length;++i) {
						for(int j=0;j<T[0].length;++j) {
							tfin[i][j]=T[i][j];
						}
					}
					boolean diff=false;
					int m=0;
					while(!diff && m<tdebut.length) {
						if(!Arrays.equals(tdebut[m],tfin[m])) diff=true; 
						m++;
					}
					if(diff==false) {
						affMines=!randomReveal();
						nbrd++;
					}
				}
				
				if(affMines) {
					lost++;
				}
				if(aGagne()) {
					vic++;
				}
				n=tmp;
				}
				System.out.println();
				System.out.println("Nombre de patie gagnee': "+vic);
				System.out.println("Nombre de patie perdue : "+lost);
				System.out.println("Nombre moyenne de revelation aleatoire: "+nbrd/f);
				while(!rc) {
					System.out.println("Recommencer Simulateur?");
					String rcc=sc.next();
					if(rcc.equals("1")) {rc=false; break;}
					else if(rcc.equals("0")) rc=true;
					else System.out.println("Veuillez saisir 1 pour Oui 0 pour Non");
					System.out.println();
				}
				}
			}
			//3-Instruction
			else if(commande.equals("3")) {
			System.out.println("Instruction:");
			System.out.println("Pour marquer la case d'un drapeau, le format est: d + numero de ligne + lettre de la colonne.");
			System.out.println("Pour reveler une case, le format est: r + numero de ligne + lettre de la colonne.");
			System.out.println("Pour reveler une case aleatoirement, veuillez saisir 'rand'.");
			System.out.println("Pour demander soutien, veuillez saisir 'aide'.");
			System.out.println("Pour lancer une IA, veuillez saisir'AI'");
			System.out.println();
			}
			//Quitter
			else if(commande.equals("4")) {
				System.out.println("Jeu termine'!");
				ingame=false;
			}
			else if(commande.equals("-1")) {
				System.out.println("50544E");
			}
			else System.out.println("Mauvaise commande,les commandes possibles sont 1-Commencer, 2-Simulateur IA, 3-Instruction et 4-Quitter");
			}
			sc.close();
	}
	
}
