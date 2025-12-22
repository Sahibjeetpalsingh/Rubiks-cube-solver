
class FaceCube {
	public Color[] f = new Color[54];

	final static Facelet[][] cornerFacelet = { { Facelet.U9, Facelet.R1, Facelet.F3 },
			{ Facelet.U7, Facelet.F1, Facelet.L3 }, { Facelet.U1, Facelet.L1, Facelet.B3 },
			{ Facelet.U3, Facelet.B1, Facelet.R3 },
			{ Facelet.D3, Facelet.F9, Facelet.R7 }, { Facelet.D1, Facelet.L9, Facelet.F7 },
			{ Facelet.D7, Facelet.B9, Facelet.L7 }, { Facelet.D9, Facelet.R9, Facelet.B7 } };

	final static Facelet[][] edgeFacelet = { { Facelet.U6, Facelet.R2 }, { Facelet.U8, Facelet.F2 },
			{ Facelet.U4, Facelet.L2 }, { Facelet.U2, Facelet.B2 }, { Facelet.D6, Facelet.R8 },
			{ Facelet.D2, Facelet.F8 },
			{ Facelet.D4, Facelet.L8 }, { Facelet.D8, Facelet.B8 }, { Facelet.F6, Facelet.R4 },
			{ Facelet.F4, Facelet.L6 }, { Facelet.B6, Facelet.L4 }, { Facelet.B4, Facelet.R6 } };

	final static Color[][] cornerColor = { { Color.U, Color.R, Color.F }, { Color.U, Color.F, Color.L },
			{ Color.U, Color.L, Color.B }, { Color.U, Color.B, Color.R }, { Color.D, Color.F, Color.R },
			{ Color.D, Color.L, Color.F },
			{ Color.D, Color.B, Color.L }, { Color.D, Color.R, Color.B } };

	final static Color[][] edgeColor = { { Color.U, Color.R }, { Color.U, Color.F }, { Color.U, Color.L },
			{ Color.U, Color.B }, { Color.D, Color.R }, { Color.D, Color.F }, { Color.D, Color.L },
			{ Color.D, Color.B },
			{ Color.F, Color.R }, { Color.F, Color.L }, { Color.B, Color.L }, { Color.B, Color.R } };

	FaceCube() {
		String s = "UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB";
		for (int i = 0; i < 54; i++)
			f[i] = Color.valueOf(s.substring(i, i + 1));

	}

	FaceCube(String cubeString) {
		for (int i = 0; i < cubeString.length(); i++)
			f[i] = Color.valueOf(cubeString.substring(i, i + 1));
	}

	String to_String() {
		String s = "";
		for (int i = 0; i < 54; i++)
			s += f[i].toString();
		return s;
	}

	CubieCube toCubieCube() {
		byte ori;
		CubieCube ccRet = new CubieCube();
		for (int i = 0; i < 8; i++)
			ccRet.cp[i] = Corner.URF;
		for (int i = 0; i < 12; i++)
			ccRet.ep[i] = Edge.UR;
		Color col1, col2;
		for (Corner i : Corner.values()) {

			for (ori = 0; ori < 3; ori++)
				if (f[cornerFacelet[i.ordinal()][ori].ordinal()] == Color.U
						|| f[cornerFacelet[i.ordinal()][ori].ordinal()] == Color.D)
					break;
			col1 = f[cornerFacelet[i.ordinal()][(ori + 1) % 3].ordinal()];
			col2 = f[cornerFacelet[i.ordinal()][(ori + 2) % 3].ordinal()];

			for (Corner j : Corner.values()) {
				if (col1 == cornerColor[j.ordinal()][1] && col2 == cornerColor[j.ordinal()][2]) {

					ccRet.cp[i.ordinal()] = j;
					ccRet.co[i.ordinal()] = (byte) (ori % 3);
					break;
				}
			}
		}
		for (Edge i : Edge.values())
			for (Edge j : Edge.values()) {
				if (f[edgeFacelet[i.ordinal()][0].ordinal()] == edgeColor[j.ordinal()][0]
						&& f[edgeFacelet[i.ordinal()][1].ordinal()] == edgeColor[j.ordinal()][1]) {
					ccRet.ep[i.ordinal()] = j;
					ccRet.eo[i.ordinal()] = 0;
					break;
				}
				if (f[edgeFacelet[i.ordinal()][0].ordinal()] == edgeColor[j.ordinal()][1]
						&& f[edgeFacelet[i.ordinal()][1].ordinal()] == edgeColor[j.ordinal()][0]) {
					ccRet.ep[i.ordinal()] = j;
					ccRet.eo[i.ordinal()] = 1;
					break;
				}
			}
		return ccRet;
	};
}
