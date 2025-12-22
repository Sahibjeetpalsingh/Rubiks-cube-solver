
class CubieCube {

	Corner[] cp = { Corner.URF, Corner.UFL, Corner.ULB, Corner.UBR, Corner.DFR, Corner.DLF, Corner.DBL, Corner.DRB };

	byte[] co = { 0, 0, 0, 0, 0, 0, 0, 0 };

	Edge[] ep = { Edge.UR, Edge.UF, Edge.UL, Edge.UB, Edge.DR, Edge.DF, Edge.DL, Edge.DB, Edge.FR, Edge.FL, Edge.BL,
			Edge.BR };

	byte[] eo = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	private static Corner[] cpU = { Corner.UBR, Corner.URF, Corner.UFL, Corner.ULB, Corner.DFR, Corner.DLF, Corner.DBL,
			Corner.DRB };
	private static byte[] coU = { 0, 0, 0, 0, 0, 0, 0, 0 };
	private static Edge[] epU = { Edge.UB, Edge.UR, Edge.UF, Edge.UL, Edge.DR, Edge.DF, Edge.DL, Edge.DB, Edge.FR,
			Edge.FL, Edge.BL, Edge.BR };
	private static byte[] eoU = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	private static Corner[] cpR = { Corner.DFR, Corner.UFL, Corner.ULB, Corner.URF, Corner.DRB, Corner.DLF, Corner.DBL,
			Corner.UBR };
	private static byte[] coR = { 2, 0, 0, 1, 1, 0, 0, 2 };
	private static Edge[] epR = { Edge.FR, Edge.UF, Edge.UL, Edge.UB, Edge.BR, Edge.DF, Edge.DL, Edge.DB, Edge.DR,
			Edge.FL, Edge.BL, Edge.UR };
	private static byte[] eoR = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	private static Corner[] cpF = { Corner.UFL, Corner.DLF, Corner.ULB, Corner.UBR, Corner.URF, Corner.DFR, Corner.DBL,
			Corner.DRB };
	private static byte[] coF = { 1, 2, 0, 0, 2, 1, 0, 0 };
	private static Edge[] epF = { Edge.UR, Edge.FL, Edge.UL, Edge.UB, Edge.DR, Edge.FR, Edge.DL, Edge.DB, Edge.UF,
			Edge.DF, Edge.BL, Edge.BR };
	private static byte[] eoF = { 0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0 };

	private static Corner[] cpD = { Corner.URF, Corner.UFL, Corner.ULB, Corner.UBR, Corner.DLF, Corner.DBL, Corner.DRB,
			Corner.DFR };
	private static byte[] coD = { 0, 0, 0, 0, 0, 0, 0, 0 };
	private static Edge[] epD = { Edge.UR, Edge.UF, Edge.UL, Edge.UB, Edge.DF, Edge.DL, Edge.DB, Edge.DR, Edge.FR,
			Edge.FL, Edge.BL, Edge.BR };
	private static byte[] eoD = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	private static Corner[] cpL = { Corner.URF, Corner.ULB, Corner.DBL, Corner.UBR, Corner.DFR, Corner.UFL, Corner.DLF,
			Corner.DRB };
	private static byte[] coL = { 0, 1, 2, 0, 0, 2, 1, 0 };
	private static Edge[] epL = { Edge.UR, Edge.UF, Edge.BL, Edge.UB, Edge.DR, Edge.DF, Edge.FL, Edge.DB, Edge.FR,
			Edge.UL, Edge.DL, Edge.BR };
	private static byte[] eoL = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	private static Corner[] cpB = { Corner.URF, Corner.UFL, Corner.UBR, Corner.DRB, Corner.DFR, Corner.DLF, Corner.ULB,
			Corner.DBL };
	private static byte[] coB = { 0, 0, 1, 2, 0, 0, 2, 1 };
	private static Edge[] epB = { Edge.UR, Edge.UF, Edge.UL, Edge.BR, Edge.DR, Edge.DF, Edge.DL, Edge.BL, Edge.FR,
			Edge.FL, Edge.UB, Edge.DB };
	private static byte[] eoB = { 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 1 };

	static CubieCube[] moveCube = new CubieCube[6];

	static {
		moveCube[0] = new CubieCube();
		moveCube[0].cp = cpU;
		moveCube[0].co = coU;
		moveCube[0].ep = epU;
		moveCube[0].eo = eoU;

		moveCube[1] = new CubieCube();
		moveCube[1].cp = cpR;
		moveCube[1].co = coR;
		moveCube[1].ep = epR;
		moveCube[1].eo = eoR;

		moveCube[2] = new CubieCube();
		moveCube[2].cp = cpF;
		moveCube[2].co = coF;
		moveCube[2].ep = epF;
		moveCube[2].eo = eoF;

		moveCube[3] = new CubieCube();
		moveCube[3].cp = cpD;
		moveCube[3].co = coD;
		moveCube[3].ep = epD;
		moveCube[3].eo = eoD;

		moveCube[4] = new CubieCube();
		moveCube[4].cp = cpL;
		moveCube[4].co = coL;
		moveCube[4].ep = epL;
		moveCube[4].eo = eoL;

		moveCube[5] = new CubieCube();
		moveCube[5].cp = cpB;
		moveCube[5].co = coB;
		moveCube[5].ep = epB;
		moveCube[5].eo = eoB;

	}

	CubieCube() {

	};

	CubieCube(Corner[] cp, byte[] co, Edge[] ep, byte[] eo) {
		this();
		for (int i = 0; i < 8; i++) {
			this.cp[i] = cp[i];
			this.co[i] = co[i];
		}
		for (int i = 0; i < 12; i++) {
			this.ep[i] = ep[i];
			this.eo[i] = eo[i];
		}
	}

	static int Cnk(int n, int k) {
		int i, j, s;
		if (n < k)
			return 0;
		if (k > n / 2)
			k = n - k;
		for (s = 1, i = n, j = 1; i != n - k; i--, j++) {
			s *= i;
			s /= j;
		}
		return s;
	}

	static void rotateLeft(Corner[] arr, int l, int r)

	{
		Corner temp = arr[l];
		for (int i = l; i < r; i++)
			arr[i] = arr[i + 1];
		arr[r] = temp;
	}

	static void rotateRight(Corner[] arr, int l, int r)

	{
		Corner temp = arr[r];
		for (int i = r; i > l; i--)
			arr[i] = arr[i - 1];
		arr[l] = temp;
	}

	static void rotateLeft(Edge[] arr, int l, int r)

	{
		Edge temp = arr[l];
		for (int i = l; i < r; i++)
			arr[i] = arr[i + 1];
		arr[r] = temp;
	}

	static void rotateRight(Edge[] arr, int l, int r)

	{
		Edge temp = arr[r];
		for (int i = r; i > l; i--)
			arr[i] = arr[i - 1];
		arr[l] = temp;
	}

	FaceCube toFaceCube() {
		FaceCube fcRet = new FaceCube();
		for (Corner c : Corner.values()) {
			int i = c.ordinal();
			int j = cp[i].ordinal();

			byte ori = co[i];
			for (int n = 0; n < 3; n++)
				fcRet.f[FaceCube.cornerFacelet[i][(n + ori) % 3].ordinal()] = FaceCube.cornerColor[j][n];
		}
		for (Edge e : Edge.values()) {
			int i = e.ordinal();
			int j = ep[i].ordinal();

			byte ori = eo[i];
			for (int n = 0; n < 2; n++)
				fcRet.f[FaceCube.edgeFacelet[i][(n + ori) % 2].ordinal()] = FaceCube.edgeColor[j][n];
		}
		return fcRet;
	}

	void cornerMultiply(CubieCube b) {
		Corner[] cPerm = new Corner[8];
		byte[] cOri = new byte[8];
		for (Corner corn : Corner.values()) {
			cPerm[corn.ordinal()] = cp[b.cp[corn.ordinal()].ordinal()];

			byte oriA = co[b.cp[corn.ordinal()].ordinal()];
			byte oriB = b.co[corn.ordinal()];
			byte ori = 0;
			;
			if (oriA < 3 && oriB < 3) {
				ori = (byte) (oriA + oriB);
				if (ori >= 3)
					ori -= 3;

			} else if (oriA < 3 && oriB >= 3)

			{
				ori = (byte) (oriA + oriB);
				if (ori >= 6)
					ori -= 3;
			} else if (oriA >= 3 && oriB < 3)

			{
				ori = (byte) (oriA - oriB);
				if (ori < 3)
					ori += 3;
			} else if (oriA >= 3 && oriB >= 3)

			{
				ori = (byte) (oriA - oriB);
				if (ori < 0)
					ori += 3;

			}
			cOri[corn.ordinal()] = ori;
		}
		for (Corner c : Corner.values()) {
			cp[c.ordinal()] = cPerm[c.ordinal()];
			co[c.ordinal()] = cOri[c.ordinal()];
		}
	}

	void edgeMultiply(CubieCube b) {
		Edge[] ePerm = new Edge[12];
		byte[] eOri = new byte[12];
		for (Edge edge : Edge.values()) {
			ePerm[edge.ordinal()] = ep[b.ep[edge.ordinal()].ordinal()];
			eOri[edge.ordinal()] = (byte) ((b.eo[edge.ordinal()] + eo[b.ep[edge.ordinal()].ordinal()]) % 2);
		}
		for (Edge e : Edge.values()) {
			ep[e.ordinal()] = ePerm[e.ordinal()];
			eo[e.ordinal()] = eOri[e.ordinal()];
		}
	}

	void multiply(CubieCube b) {
		cornerMultiply(b);

	}

	void invCubieCube(CubieCube c) {
		for (Edge edge : Edge.values())
			c.ep[ep[edge.ordinal()].ordinal()] = edge;
		for (Edge edge : Edge.values())
			c.eo[edge.ordinal()] = eo[c.ep[edge.ordinal()].ordinal()];
		for (Corner corn : Corner.values())
			c.cp[cp[corn.ordinal()].ordinal()] = corn;
		for (Corner corn : Corner.values()) {
			byte ori = co[c.cp[corn.ordinal()].ordinal()];
			if (ori >= 3)

				c.co[corn.ordinal()] = ori;
			else {
				c.co[corn.ordinal()] = (byte) -ori;
				if (c.co[corn.ordinal()] < 0)
					c.co[corn.ordinal()] += 3;
			}
		}
	}

	short getTwist() {
		short ret = 0;
		for (int i = Corner.URF.ordinal(); i < Corner.DRB.ordinal(); i++)
			ret = (short) (3 * ret + co[i]);
		return ret;
	}

	void setTwist(short twist) {
		int twistParity = 0;
		for (int i = Corner.DRB.ordinal() - 1; i >= Corner.URF.ordinal(); i--) {
			twistParity += co[i] = (byte) (twist % 3);
			twist /= 3;
		}
		co[Corner.DRB.ordinal()] = (byte) ((3 - twistParity % 3) % 3);
	}

	short getFlip() {
		short ret = 0;
		for (int i = Edge.UR.ordinal(); i < Edge.BR.ordinal(); i++)
			ret = (short) (2 * ret + eo[i]);
		return ret;
	}

	void setFlip(short flip) {
		int flipParity = 0;
		for (int i = Edge.BR.ordinal() - 1; i >= Edge.UR.ordinal(); i--) {
			flipParity += eo[i] = (byte) (flip % 2);
			flip /= 2;
		}
		eo[Edge.BR.ordinal()] = (byte) ((2 - flipParity % 2) % 2);
	}

	short cornerParity() {
		int s = 0;
		for (int i = Corner.DRB.ordinal(); i >= Corner.URF.ordinal() + 1; i--)
			for (int j = i - 1; j >= Corner.URF.ordinal(); j--)
				if (cp[j].ordinal() > cp[i].ordinal())
					s++;
		return (short) (s % 2);
	}

	short edgeParity() {
		int s = 0;
		for (int i = Edge.BR.ordinal(); i >= Edge.UR.ordinal() + 1; i--)
			for (int j = i - 1; j >= Edge.UR.ordinal(); j--)
				if (ep[j].ordinal() > ep[i].ordinal())
					s++;
		return (short) (s % 2);
	}

	short getFRtoBR() {
		int a = 0, x = 0;
		Edge[] edge4 = new Edge[4];

		for (int j = Edge.BR.ordinal(); j >= Edge.UR.ordinal(); j--)
			if (Edge.FR.ordinal() <= ep[j].ordinal() && ep[j].ordinal() <= Edge.BR.ordinal()) {
				a += Cnk(11 - j, x + 1);
				edge4[3 - x++] = ep[j];
			}

		int b = 0;
		for (int j = 3; j > 0; j--)

		{
			int k = 0;
			while (edge4[j].ordinal() != j + 8) {
				rotateLeft(edge4, 0, j);
				k++;
			}
			b = (j + 1) * b + k;
		}
		return (short) (24 * a + b);
	}

	void setFRtoBR(short idx) {
		int x;
		Edge[] sliceEdge = { Edge.FR, Edge.FL, Edge.BL, Edge.BR };
		Edge[] otherEdge = { Edge.UR, Edge.UF, Edge.UL, Edge.UB, Edge.DR, Edge.DF, Edge.DL, Edge.DB };
		int b = idx % 24;
		int a = idx / 24;
		for (Edge e : Edge.values())
			ep[e.ordinal()] = Edge.DB;

		for (int j = 1, k; j < 4; j++) {
			k = b % (j + 1);
			b /= j + 1;
			while (k-- > 0)
				rotateRight(sliceEdge, 0, j);
		}

		x = 3;
		for (int j = Edge.UR.ordinal(); j <= Edge.BR.ordinal(); j++)
			if (a - Cnk(11 - j, x + 1) >= 0) {
				ep[j] = sliceEdge[3 - x];
				a -= Cnk(11 - j, x-- + 1);
			}
		x = 0;
		for (int j = Edge.UR.ordinal(); j <= Edge.BR.ordinal(); j++)
			if (ep[j] == Edge.DB)
				ep[j] = otherEdge[x++];

	}

	short getURFtoDLF() {
		int a = 0, x = 0;
		Corner[] corner6 = new Corner[6];

		for (int j = Corner.URF.ordinal(); j <= Corner.DRB.ordinal(); j++)
			if (cp[j].ordinal() <= Corner.DLF.ordinal()) {
				a += Cnk(j, x + 1);
				corner6[x++] = cp[j];
			}

		int b = 0;
		for (int j = 5; j > 0; j--)

		{
			int k = 0;
			while (corner6[j].ordinal() != j) {
				rotateLeft(corner6, 0, j);
				k++;
			}
			b = (j + 1) * b + k;
		}
		return (short) (720 * a + b);
	}

	void setURFtoDLF(short idx) {
		int x;
		Corner[] corner6 = { Corner.URF, Corner.UFL, Corner.ULB, Corner.UBR, Corner.DFR, Corner.DLF };
		Corner[] otherCorner = { Corner.DBL, Corner.DRB };
		int b = idx % 720;
		int a = idx / 720;
		for (Corner c : Corner.values())
			cp[c.ordinal()] = Corner.DRB;

		for (int j = 1, k; j < 6; j++) {
			k = b % (j + 1);
			b /= j + 1;
			while (k-- > 0)
				rotateRight(corner6, 0, j);
		}
		x = 5;
		for (int j = Corner.DRB.ordinal(); j >= 0; j--)
			if (a - Cnk(j, x + 1) >= 0) {
				cp[j] = corner6[x];
				a -= Cnk(j, x-- + 1);
			}
		x = 0;
		for (int j = Corner.URF.ordinal(); j <= Corner.DRB.ordinal(); j++)
			if (cp[j] == Corner.DRB)
				cp[j] = otherCorner[x++];
	}

	int getURtoDF() {
		int a = 0, x = 0;
		Edge[] edge6 = new Edge[6];

		for (int j = Edge.UR.ordinal(); j <= Edge.BR.ordinal(); j++)
			if (ep[j].ordinal() <= Edge.DF.ordinal()) {
				a += Cnk(j, x + 1);
				edge6[x++] = ep[j];
			}

		int b = 0;
		for (int j = 5; j > 0; j--)

		{
			int k = 0;
			while (edge6[j].ordinal() != j) {
				rotateLeft(edge6, 0, j);
				k++;
			}
			b = (j + 1) * b + k;
		}
		return 720 * a + b;
	}

	void setURtoDF(int idx) {
		int x;
		Edge[] edge6 = { Edge.UR, Edge.UF, Edge.UL, Edge.UB, Edge.DR, Edge.DF };
		Edge[] otherEdge = { Edge.DL, Edge.DB, Edge.FR, Edge.FL, Edge.BL, Edge.BR };
		int b = idx % 720;
		int a = idx / 720;
		for (Edge e : Edge.values())
			ep[e.ordinal()] = Edge.BR;

		for (int j = 1, k; j < 6; j++) {
			k = b % (j + 1);
			b /= j + 1;
			while (k-- > 0)
				rotateRight(edge6, 0, j);
		}
		x = 5;
		for (int j = Edge.BR.ordinal(); j >= 0; j--)
			if (a - Cnk(j, x + 1) >= 0) {
				ep[j] = edge6[x];
				a -= Cnk(j, x-- + 1);
			}
		x = 0;
		for (int j = Edge.UR.ordinal(); j <= Edge.BR.ordinal(); j++)
			if (ep[j] == Edge.BR)
				ep[j] = otherEdge[x++];
	}

	public static int getURtoDF(short idx1, short idx2) {
		CubieCube a = new CubieCube();
		CubieCube b = new CubieCube();
		a.setURtoUL(idx1);
		b.setUBtoDF(idx2);
		for (int i = 0; i < 8; i++) {
			if (a.ep[i] != Edge.BR)
				if (b.ep[i] != Edge.BR)
					return -1;
				else
					b.ep[i] = a.ep[i];
		}
		return b.getURtoDF();
	}

	short getURtoUL() {
		int a = 0, x = 0;
		Edge[] edge3 = new Edge[3];

		for (int j = Edge.UR.ordinal(); j <= Edge.BR.ordinal(); j++)
			if (ep[j].ordinal() <= Edge.UL.ordinal()) {
				a += Cnk(j, x + 1);
				edge3[x++] = ep[j];
			}

		int b = 0;
		for (int j = 2; j > 0; j--)

		{
			int k = 0;
			while (edge3[j].ordinal() != j) {
				rotateLeft(edge3, 0, j);
				k++;
			}
			b = (j + 1) * b + k;
		}
		return (short) (6 * a + b);
	}

	void setURtoUL(short idx) {
		int x;
		Edge[] edge3 = { Edge.UR, Edge.UF, Edge.UL };
		int b = idx % 6;
		int a = idx / 6;
		for (Edge e : Edge.values())
			ep[e.ordinal()] = Edge.BR;

		for (int j = 1, k; j < 3; j++) {
			k = b % (j + 1);
			b /= j + 1;
			while (k-- > 0)
				rotateRight(edge3, 0, j);
		}
		x = 2;
		for (int j = Edge.BR.ordinal(); j >= 0; j--)
			if (a - Cnk(j, x + 1) >= 0) {
				ep[j] = edge3[x];
				a -= Cnk(j, x-- + 1);
			}
	}

	short getUBtoDF() {
		int a = 0, x = 0;
		Edge[] edge3 = new Edge[3];

		for (int j = Edge.UR.ordinal(); j <= Edge.BR.ordinal(); j++)
			if (Edge.UB.ordinal() <= ep[j].ordinal() && ep[j].ordinal() <= Edge.DF.ordinal()) {
				a += Cnk(j, x + 1);
				edge3[x++] = ep[j];
			}

		int b = 0;
		for (int j = 2; j > 0; j--)

		{
			int k = 0;
			while (edge3[j].ordinal() != Edge.UB.ordinal() + j) {
				rotateLeft(edge3, 0, j);
				k++;
			}
			b = (j + 1) * b + k;
		}
		return (short) (6 * a + b);
	}

	void setUBtoDF(short idx) {
		int x;
		Edge[] edge3 = { Edge.UB, Edge.DR, Edge.DF };
		int b = idx % 6;
		int a = idx / 6;
		for (Edge e : Edge.values())
			ep[e.ordinal()] = Edge.BR;

		for (int j = 1, k; j < 3; j++) {
			k = b % (j + 1);
			b /= j + 1;
			while (k-- > 0)
				rotateRight(edge3, 0, j);
		}
		x = 2;
		for (int j = Edge.BR.ordinal(); j >= 0; j--)
			if (a - Cnk(j, x + 1) >= 0) {
				ep[j] = edge3[x];
				a -= Cnk(j, x-- + 1);
			}
	}

	int getURFtoDLB() {
		Corner[] perm = new Corner[8];
		int b = 0;
		for (int i = 0; i < 8; i++)
			perm[i] = cp[i];
		for (int j = 7; j > 0; j--) {
			int k = 0;
			while (perm[j].ordinal() != j) {
				rotateLeft(perm, 0, j);
				k++;
			}
			b = (j + 1) * b + k;
		}
		return b;
	}

	void setURFtoDLB(int idx) {
		Corner[] perm = { Corner.URF, Corner.UFL, Corner.ULB, Corner.UBR, Corner.DFR, Corner.DLF, Corner.DBL,
				Corner.DRB };
		int k;
		for (int j = 1; j < 8; j++) {
			k = idx % (j + 1);
			idx /= j + 1;
			while (k-- > 0)
				rotateRight(perm, 0, j);
		}
		int x = 7;
		for (int j = 7; j >= 0; j--)
			cp[j] = perm[x--];
	}

	int getURtoBR() {
		Edge[] perm = new Edge[12];
		int b = 0;
		for (int i = 0; i < 12; i++)
			perm[i] = ep[i];
		for (int j = 11; j > 0; j--) {
			int k = 0;
			while (perm[j].ordinal() != j) {
				rotateLeft(perm, 0, j);
				k++;
			}
			b = (j + 1) * b + k;
		}
		return b;
	}

	void setURtoBR(int idx) {
		Edge[] perm = { Edge.UR, Edge.UF, Edge.UL, Edge.UB, Edge.DR, Edge.DF, Edge.DL, Edge.DB, Edge.FR, Edge.FL,
				Edge.BL, Edge.BR };
		int k;
		for (int j = 1; j < 12; j++) {
			k = idx % (j + 1);
			idx /= j + 1;
			while (k-- > 0)
				rotateRight(perm, 0, j);
		}
		int x = 11;
		for (int j = 11; j >= 0; j--)
			ep[j] = perm[x--];
	}

	int verify() {
		int sum = 0;
		int[] edgeCount = new int[12];
		for (Edge e : Edge.values())
			edgeCount[ep[e.ordinal()].ordinal()]++;
		for (int i = 0; i < 12; i++)
			if (edgeCount[i] != 1)
				return -2;

		for (int i = 0; i < 12; i++)
			sum += eo[i];
		if (sum % 2 != 0)
			return -3;

		int[] cornerCount = new int[8];
		for (Corner c : Corner.values())
			cornerCount[cp[c.ordinal()].ordinal()]++;
		for (int i = 0; i < 8; i++)
			if (cornerCount[i] != 1)
				return -4;

		sum = 0;
		for (int i = 0; i < 8; i++)
			sum += co[i];
		if (sum % 3 != 0)
			return -5;

		if ((edgeParity() ^ cornerParity()) != 0)
			return -6;

		return 0;
	}
}
