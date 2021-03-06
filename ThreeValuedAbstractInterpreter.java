//Three-valued abstract interpreter
//@author Rolf Rolles
//@category Deobfuscation
//@keybinding 
//@menupath 
//@toolbar 

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.UnaryOperator; 
import java.util.function.BinaryOperator; 
import java.util.LinkedList;
import ghidra.app.script.GhidraScript;
import ghidra.program.disassemble.Disassembler;
import ghidra.program.model.lang.Language;
import ghidra.program.model.lang.Register;
import ghidra.program.model.address.AddressSetView;
import ghidra.program.model.listing.*;
import ghidra.program.model.pcode.PcodeOp;
import ghidra.program.model.pcode.Varnode;
import ghidra.program.model.pcode.VarnodeTranslator;
import ghidra.app.services.ConsoleService;
import ghidra.framework.plugintool.PluginTool;

final class Printer {
	private Printer() {};
	static ConsoleService con;
	static void Set(ConsoleService c) { con = c; }
	static void println(String s) { con.println(s); }
}

class Pair<X, Y> { 
  public final X x; 
  public final Y y; 
  public Pair(X x, Y y) { 
    this.x = x; 
    this.y = y; 
  } 
} 

class VisitorUnimplementedException extends Exception { 
    public VisitorUnimplementedException(String errorMessage) {
        super(errorMessage);
    }
}

class PcodeOpVisitor<T> {
	void VisitorBefore(Instruction instr, PcodeOp pcode) {};
	void VisitorAfter (Instruction instr, PcodeOp pcode) {};
	
	public void visit(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException
	{
		VisitorBefore(instr, pcode);
		switch(pcode.getOpcode())
		{
			case PcodeOp.BOOL_AND: visit_BOOL_AND(instr, pcode); break; 
			case PcodeOp.BOOL_NEGATE: visit_BOOL_NEGATE(instr, pcode); break; 
			case PcodeOp.BOOL_OR: visit_BOOL_OR(instr, pcode); break; 
			case PcodeOp.BOOL_XOR: visit_BOOL_XOR(instr, pcode); break; 
			case PcodeOp.BRANCH: visit_BRANCH(instr, pcode); break; 
			case PcodeOp.BRANCHIND: visit_BRANCHIND(instr, pcode); break; 
			case PcodeOp.CALL: visit_CALL(instr, pcode); break; 
			case PcodeOp.CALLIND: visit_CALLIND(instr, pcode); break; 
			case PcodeOp.CALLOTHER: visit_CALLOTHER(instr, pcode); break; 
			case PcodeOp.CAST: visit_CAST(instr, pcode); break; 
			case PcodeOp.CBRANCH: visit_CBRANCH(instr, pcode); break; 
			case PcodeOp.COPY: visit_COPY(instr, pcode); break; 
			case PcodeOp.CPOOLREF: visit_CPOOLREF(instr, pcode); break; 
			case PcodeOp.FLOAT_ABS: visit_FLOAT_ABS(instr, pcode); break; 
			case PcodeOp.FLOAT_ADD: visit_FLOAT_ADD(instr, pcode); break; 
			case PcodeOp.FLOAT_CEIL: visit_FLOAT_CEIL(instr, pcode); break; 
			case PcodeOp.FLOAT_DIV: visit_FLOAT_DIV(instr, pcode); break; 
			case PcodeOp.FLOAT_EQUAL: visit_FLOAT_EQUAL(instr, pcode); break; 
			case PcodeOp.FLOAT_FLOAT2FLOAT: visit_FLOAT_FLOAT2FLOAT(instr, pcode); break; 
			case PcodeOp.FLOAT_FLOOR: visit_FLOAT_FLOOR(instr, pcode); break; 
			case PcodeOp.FLOAT_INT2FLOAT: visit_FLOAT_INT2FLOAT(instr, pcode); break; 
			case PcodeOp.FLOAT_LESS: visit_FLOAT_LESS(instr, pcode); break; 
			case PcodeOp.FLOAT_LESSEQUAL: visit_FLOAT_LESSEQUAL(instr, pcode); break; 
			case PcodeOp.FLOAT_MULT: visit_FLOAT_MULT(instr, pcode); break; 
			case PcodeOp.FLOAT_NAN: visit_FLOAT_NAN(instr, pcode); break; 
			case PcodeOp.FLOAT_NEG: visit_FLOAT_NEG(instr, pcode); break; 
			case PcodeOp.FLOAT_NOTEQUAL: visit_FLOAT_NOTEQUAL(instr, pcode); break; 
			case PcodeOp.FLOAT_ROUND: visit_FLOAT_ROUND(instr, pcode); break; 
			case PcodeOp.FLOAT_SQRT: visit_FLOAT_SQRT(instr, pcode); break; 
			case PcodeOp.FLOAT_SUB: visit_FLOAT_SUB(instr, pcode); break; 
			case PcodeOp.FLOAT_TRUNC: visit_FLOAT_TRUNC(instr, pcode); break; 
			case PcodeOp.INDIRECT: visit_INDIRECT(instr, pcode); break; 
			case PcodeOp.INT_2COMP: visit_INT_2COMP(instr, pcode); break; 
			case PcodeOp.INT_ADD: visit_INT_ADD(instr, pcode); break; 
			case PcodeOp.INT_AND: visit_INT_AND(instr, pcode); break; 
			case PcodeOp.INT_CARRY: visit_INT_CARRY(instr, pcode); break; 
			case PcodeOp.INT_DIV: visit_INT_DIV(instr, pcode); break; 
			case PcodeOp.INT_EQUAL: visit_INT_EQUAL(instr, pcode); break; 
			case PcodeOp.INT_LEFT: visit_INT_LEFT(instr, pcode); break; 
			case PcodeOp.INT_LESS: visit_INT_LESS(instr, pcode); break; 
			case PcodeOp.INT_LESSEQUAL: visit_INT_LESSEQUAL(instr, pcode); break; 
			case PcodeOp.INT_MULT: visit_INT_MULT(instr, pcode); break; 
			case PcodeOp.INT_NEGATE: visit_INT_NEGATE(instr, pcode); break; 
			case PcodeOp.INT_NOTEQUAL: visit_INT_NOTEQUAL(instr, pcode); break; 
			case PcodeOp.INT_OR: visit_INT_OR(instr, pcode); break; 
			case PcodeOp.INT_REM: visit_INT_REM(instr, pcode); break; 
			case PcodeOp.INT_RIGHT: visit_INT_RIGHT(instr, pcode); break; 
			case PcodeOp.INT_SBORROW: visit_INT_SBORROW(instr, pcode); break; 
			case PcodeOp.INT_SCARRY: visit_INT_SCARRY(instr, pcode); break; 
			case PcodeOp.INT_SDIV: visit_INT_SDIV(instr, pcode); break; 
			case PcodeOp.INT_SEXT: visit_INT_SEXT(instr, pcode); break; 
			case PcodeOp.INT_SLESS: visit_INT_SLESS(instr, pcode); break; 
			case PcodeOp.INT_SLESSEQUAL: visit_INT_SLESSEQUAL(instr, pcode); break; 
			case PcodeOp.INT_SREM: visit_INT_SREM(instr, pcode); break; 
			case PcodeOp.INT_SRIGHT: visit_INT_SRIGHT(instr, pcode); break; 
			case PcodeOp.INT_SUB: visit_INT_SUB(instr, pcode); break; 
			case PcodeOp.INT_XOR: visit_INT_XOR(instr, pcode); break; 
			case PcodeOp.INT_ZEXT: visit_INT_ZEXT(instr, pcode); break; 
			case PcodeOp.LOAD: visit_LOAD(instr, pcode); break; 
			case PcodeOp.MULTIEQUAL: visit_MULTIEQUAL(instr, pcode); break; 
			case PcodeOp.NEW: visit_NEW(instr, pcode); break; 
			case PcodeOp.PIECE: visit_PIECE(instr, pcode); break; 
			case PcodeOp.PTRADD: visit_PTRADD(instr, pcode); break; 
			case PcodeOp.PTRSUB: visit_PTRSUB(instr, pcode); break; 
			case PcodeOp.RETURN: visit_RETURN(instr, pcode); break; 
			case PcodeOp.SEGMENTOP: visit_SEGMENTOP(instr, pcode); break; 
			case PcodeOp.STORE: visit_STORE(instr, pcode); break; 
			case PcodeOp.SUBPIECE: visit_SUBPIECE(instr, pcode); break; 
			case PcodeOp.UNIMPLEMENTED: visit_UNIMPLEMENTED(instr, pcode); break;	
		}
		VisitorAfter(instr, pcode);
	}
	void VisitorUnimplemented(String s) throws VisitorUnimplementedException
	{
		throw new VisitorUnimplementedException("Visitor did not implement "+s);
	}
	
	T visit_Address(Instruction instr, PcodeOp pcode, Varnode Address) throws VisitorUnimplementedException 
	{
		VisitorUnimplemented("Address"); 
		return null;
	}
	T visit_AddrTied(Instruction instr, PcodeOp pcode, Varnode AddrTied) throws VisitorUnimplementedException 
	{
		VisitorUnimplemented("AddrTied"); 
		return null;
	}
	
	T visit_Constant(Instruction instr, PcodeOp pcode, Varnode Constant) throws VisitorUnimplementedException 
	{
		VisitorUnimplemented("Constant"); 
		return null;
	}

	T visit_Free(Instruction instr, PcodeOp pcode, Varnode Free) throws VisitorUnimplementedException 
	{
		VisitorUnimplemented("Free"); 
		return null;
	}
	T visit_Hash(Instruction instr, PcodeOp pcode, Varnode Hash) throws VisitorUnimplementedException 
	{
		VisitorUnimplemented("Hash"); 
		return null;
	}
	T visit_Input(Instruction instr, PcodeOp pcode, Varnode Input) throws VisitorUnimplementedException 
	{
		VisitorUnimplemented("Input"); 
		return null;
	}

	T visit_Persistant(Instruction instr, PcodeOp pcode, Varnode Persistant) throws VisitorUnimplementedException 
	{
		VisitorUnimplemented("Persistant"); 
		return null;
	}

	T visit_Register(Instruction instr, PcodeOp pcode, Varnode Register) throws VisitorUnimplementedException 
	{
		VisitorUnimplemented("Register"); 
		return null;
	}
	T visit_Unaffected(Instruction instr, PcodeOp pcode, Varnode Unaffected) throws VisitorUnimplementedException 
	{
		VisitorUnimplemented("Unaffected"); 
		return null;
	}
	T visit_Unique(Instruction instr, PcodeOp pcode, Varnode Unique) throws VisitorUnimplementedException 
	{
		VisitorUnimplemented("Unique"); 
		return null;
	}
	
	T visit_Varnode(Instruction instr, PcodeOp pcode, Varnode varnode) throws VisitorUnimplementedException
	{
		boolean isAddress    = varnode.isAddress();	 
		boolean isAddrTied   = varnode.isAddrTied();	 
		boolean isConstant   = varnode.isConstant();	 
		boolean isHash       = varnode.isHash();
		boolean isInput      = varnode.isInput();	 
		boolean isPersistant = varnode.isPersistant();	 
		boolean isRegister   = varnode.isRegister();	 
		boolean isUnaffected = varnode.isUnaffected();	 
		boolean isUnique     = varnode.isUnique();	
		if(isAddress)    return visit_Address(instr, pcode, varnode);
		if(isAddrTied)   return visit_AddrTied(instr, pcode, varnode);
		if(isConstant)   return visit_Constant(instr, pcode, varnode);
		if(isHash)       return visit_Hash(instr, pcode, varnode);
		if(isInput)      return visit_Input(instr, pcode, varnode);
		if(isPersistant) return visit_Persistant(instr, pcode, varnode);
		if(isRegister)   return visit_Register(instr, pcode, varnode);
		if(isUnaffected) return visit_Unaffected(instr, pcode, varnode);
		if(isUnique)     return visit_Unique(instr, pcode, varnode);
		VisitorUnimplemented("Unknown varnode type");
		return null;
	}

	void visit_BOOL_AND(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("BOOL_AND"); }; 
	void visit_BOOL_NEGATE(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("BOOL_NEGATE"); }; 
	void visit_BOOL_OR(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("BOOL_OR"); }; 
	void visit_BOOL_XOR(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("BOOL_XOR"); }; 
	void visit_BRANCH(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("BRANCH"); }; 
	void visit_BRANCHIND(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("BRANCHIND"); }; 
	void visit_CALL(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("CALL"); }; 
	void visit_CALLIND(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("CALLIND"); }; 
	void visit_CALLOTHER(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("CALLOTHER"); }; 
	void visit_CAST(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("CAST"); }; 
	void visit_CBRANCH(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("CBRANCH"); }; 
	void visit_COPY(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("COPY"); }; 
	void visit_CPOOLREF(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("CPOOLREF"); }; 
	void visit_FLOAT_ABS(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("FLOAT_ABS"); }; 
	void visit_FLOAT_ADD(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("FLOAT_ADD"); }; 
	void visit_FLOAT_CEIL(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("FLOAT_CEIL"); }; 
	void visit_FLOAT_DIV(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("FLOAT_DIV"); }; 
	void visit_FLOAT_EQUAL(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("FLOAT_EQUAL"); }; 
	void visit_FLOAT_FLOAT2FLOAT(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("FLOAT_FLOAT2FLOAT"); }; 
	void visit_FLOAT_FLOOR(Instruction instr, PcodeOp pcode)       throws VisitorUnimplementedException { VisitorUnimplemented("FLOAT_FLOOR"); }; 
	void visit_FLOAT_INT2FLOAT(Instruction instr, PcodeOp pcode)   throws VisitorUnimplementedException { VisitorUnimplemented("FLOAT_INT2FLOAT"); }; 
	void visit_FLOAT_LESS(Instruction instr, PcodeOp pcode)        throws VisitorUnimplementedException { VisitorUnimplemented("FLOAT_LESS"); }; 
	void visit_FLOAT_LESSEQUAL(Instruction instr, PcodeOp pcode)   throws VisitorUnimplementedException { VisitorUnimplemented("FLOAT_LESSEQUAL"); }; 
	void visit_FLOAT_MULT(Instruction instr, PcodeOp pcode)        throws VisitorUnimplementedException { VisitorUnimplemented("FLOAT_MULT"); }; 
	void visit_FLOAT_NAN(Instruction instr, PcodeOp pcode)         throws VisitorUnimplementedException { VisitorUnimplemented("FLOAT_NAN"); }; 
	void visit_FLOAT_NEG(Instruction instr, PcodeOp pcode)         throws VisitorUnimplementedException { VisitorUnimplemented("FLOAT_NEG"); }; 
	void visit_FLOAT_NOTEQUAL(Instruction instr, PcodeOp pcode)    throws VisitorUnimplementedException { VisitorUnimplemented("FLOAT_NOTEQUAL"); }; 
	void visit_FLOAT_ROUND(Instruction instr, PcodeOp pcode)       throws VisitorUnimplementedException { VisitorUnimplemented("FLOAT_ROUND"); }; 
	void visit_FLOAT_SQRT(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("FLOAT_SQRT"); }; 
	void visit_FLOAT_SUB(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("FLOAT_SUB"); }; 
	void visit_FLOAT_TRUNC(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("FLOAT_TRUNC"); }; 
	void visit_INDIRECT(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INDIRECT"); }; 
	void visit_INT_2COMP(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_2COMP"); }; 
	void visit_INT_ADD(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_ADD"); }; 
	void visit_INT_AND(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_AND"); }; 
	void visit_INT_CARRY(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_CARRY"); }; 
	void visit_INT_DIV(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_DIV"); }; 
	void visit_INT_EQUAL(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_EQUAL"); }; 
	void visit_INT_LEFT(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_LEFT"); }; 
	void visit_INT_LESS(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_LESS"); }; 
	void visit_INT_LESSEQUAL(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_LESSEQUAL"); }; 
	void visit_INT_MULT(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_MULT"); }; 
	void visit_INT_NEGATE(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_NEGATE"); }; 
	void visit_INT_NOTEQUAL(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_NOTEQUAL"); }; 
	void visit_INT_OR(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_OR"); }; 
	void visit_INT_REM(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_REM"); }; 
	void visit_INT_RIGHT(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_RIGHT"); }; 
	void visit_INT_SBORROW(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_SBORROW"); }; 
	void visit_INT_SCARRY(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_SCARRY"); }; 
	void visit_INT_SDIV(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_SDIV"); }; 
	void visit_INT_SEXT(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_SEXT"); }; 
	void visit_INT_SLESS(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_SLESS"); }; 
	void visit_INT_SLESSEQUAL(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_SLESSEQUAL"); }; 
	void visit_INT_SREM(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_SREM"); }; 
	void visit_INT_SRIGHT(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_SRIGHT"); }; 
	void visit_INT_SUB(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_SUB"); }; 
	void visit_INT_XOR(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_XOR"); }; 
	void visit_INT_ZEXT(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_ZEXT"); }; 
	void visit_LOAD(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("LOAD"); }; 
	void visit_MULTIEQUAL(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("MULTIEQUAL"); }; 
	void visit_NEW(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("NEW"); }; 
	void visit_PIECE(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("PIECE"); }; 
	void visit_PTRADD(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("PTRADD"); }; 
	void visit_PTRSUB(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("PTRSUB"); }; 
	void visit_RETURN(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("RETURN"); }; 
	void visit_SEGMENTOP(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("SEGMENTOP"); }; 
	void visit_STORE(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("STORE"); }; 
	void visit_SUBPIECE(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("SUBPIECE"); }; 
	void visit_UNIMPLEMENTED(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("UNIMPLEMENTED"); }; 
}

class GhidraSizeAdapter
{
	public int sz;
	public GhidraSizeAdapter(int s) { sz = s; }
}

class TVLBitVector {
	public static final byte TVL_0    = 0;
	public static final byte TVL_HALF = 1;
	public static final byte TVL_1    = 2;
	byte AbsValue[];
	//boolean IsConstant;
	//long Constant;
	
	public int Size() { return AbsValue.length; }
	
	// Probably bad programming practice, but I don't know Java at all
	public byte[] Value() { return AbsValue; }
	
	public Pair<Integer,Long> GetConstantValue()
	{
		if(AbsValue.length > 64)
			return null;
			
		long val = 0;
		for(int i = 0; i < AbsValue.length; i++) {
			if(AbsValue[i] == TVL_HALF)
				return null;
			if(AbsValue[i] == TVL_1)
				val |= 1 << i;
		}
		return new Pair(AbsValue.length,val);
	}
	
	void MakeTop()
	{
		for(int i = 0; i < AbsValue.length; i++)
			AbsValue[i] = TVL_HALF;
	}
	
	public TVLBitVector(int sz)
	{
		AbsValue = new byte[sz];
		MakeTop();
	}
	
	public TVLBitVector(GhidraSizeAdapter gsa)
	{
		AbsValue = new byte[gsa.sz*8];
		MakeTop();
	}

	void InitializeFromConstant(int sz, long value)
	{
	  AbsValue = new byte[sz];
		for (int i = 0; i < sz; i++) {
			AbsValue[i] = ((value >> i) & 1) == 0 ? TVL_0 : TVL_1;
		}
	}	
	public TVLBitVector(int sz, long value)
	{
		InitializeFromConstant(sz,value);
	}
	public TVLBitVector(GhidraSizeAdapter gsa, long value)
	{
		InitializeFromConstant(gsa.sz*8,value);
	}

	public TVLBitVector(byte[] Arr)
	{
		AbsValue = Arr;
	}
	
	public TVLBitVector clone()
	{
		return new TVLBitVector(AbsValue.clone());
	}
	
	@Override
	public String toString()
	{
		String s = "";
		for(int i = AbsValue.length-1; i >= 0; i--)
		{
			switch(AbsValue[i])
			{
				case TVLBitVector.TVL_0:
				s += '0';
				break;
				case TVLBitVector.TVL_1:
				s += '1';
				break;
				case TVLBitVector.TVL_HALF:
				s += '?';
				break;
			}
		}
		return s;
	}
}

class TVLBitVectorSizeMismatchException extends Exception { 
	public TVLBitVectorSizeMismatchException(int s1, int s2) {
		super("Sizes "+s1+"/"+s2);
	}
}

final class TVLBitVectorUtil {
	private TVLBitVectorUtil() {};
	
	static TVLBitVector Map(TVLBitVector lhs, UnaryOperator<Byte> f)
	{
		int s1 = lhs.Size();
		
		byte[] lhsArr = lhs.Value();
		byte[] newArr = new byte[s1];
		for (int i = 0; i < s1; i++) {
			newArr[i] = f.apply(lhsArr[i]);
		}
		return new TVLBitVector(newArr);
	}

	// ~x ...
	static final byte[] NotTable = { 
		  TVLBitVector.TVL_1,    // x = 0
		  TVLBitVector.TVL_HALF, // x = 1/2
		  TVLBitVector.TVL_0,    // x = 1
	};

	static TVLBitVector Not(TVLBitVector lhs)
	{
		return Map(lhs, (l) -> NotTable[l]);
	}

	static void SizeMismatchException(String op, int s1, int s2)
	{
		throw new RuntimeException("TVLBitVector: "+op+" sizes "+s1+"/"+s2);
	}
	
	static TVLBitVector Map2(TVLBitVector lhs, TVLBitVector rhs, BinaryOperator<Byte> f) 
	{
		int s1 = lhs.Size();
		int s2 = rhs.Size();
		if(s1 != s2)
			SizeMismatchException("map2", s1, s2);
		
		byte[] lhsArr = lhs.Value();
		byte[] rhsArr = rhs.Value();
		byte[] newArr = new byte[s1];
		for (int i = 0; i < s1; i++) {
			newArr[i] = f.apply(lhsArr[i], rhsArr[i]);
		}
		return new TVLBitVector(newArr);
	}
	
	// x & y ...
	static final byte[][] AndTable = { 
		//     y = 0                   y = 1/2                y = 1
		{TVLBitVector.TVL_0,     TVLBitVector.TVL_0,    TVLBitVector.TVL_0},    // x = 0
		{TVLBitVector.TVL_0,     TVLBitVector.TVL_HALF, TVLBitVector.TVL_HALF}, // x = 1/2
		{TVLBitVector.TVL_0,     TVLBitVector.TVL_HALF, TVLBitVector.TVL_1}     // x = 1
	};
	// x | y ...
	static final byte[][] OrTable = { 
		//     y = 0                   y = 1/2                y = 1
		{TVLBitVector.TVL_0,     TVLBitVector.TVL_HALF, TVLBitVector.TVL_1},    // x = 0
		{TVLBitVector.TVL_HALF,  TVLBitVector.TVL_HALF, TVLBitVector.TVL_1},    // x = 1/2
		{TVLBitVector.TVL_1,     TVLBitVector.TVL_1,    TVLBitVector.TVL_1},    // x = 1
	};
	// x ^ y ...
	static final byte[][] XorTable = { 
		//     y = 0                   y = 1/2                y = 1
		{TVLBitVector.TVL_0,     TVLBitVector.TVL_HALF, TVLBitVector.TVL_1},    // x = 0
		{TVLBitVector.TVL_HALF,  TVLBitVector.TVL_HALF, TVLBitVector.TVL_HALF}, // x = 1/2
		{TVLBitVector.TVL_1,     TVLBitVector.TVL_HALF, TVLBitVector.TVL_0},    // x = 1
	};
	static TVLBitVector And(TVLBitVector lhs, TVLBitVector rhs) 
	{
		return Map2(lhs, rhs, (l,r) -> AndTable[l][r]);
	}

	static TVLBitVector Or(TVLBitVector lhs, TVLBitVector rhs) 
	{
		return Map2(lhs, rhs, (l,r) -> OrTable[l][r]);
	}

	static TVLBitVector Xor(TVLBitVector lhs, TVLBitVector rhs) 
	{
		return Map2(lhs, rhs, (l,r) -> XorTable[l][r]);
	}
	
	static TVLBitVector Extend(TVLBitVector lhs, int newSize, byte extensionVal)
	{
		int lhsSize = lhs.Size();
		
		if(lhsSize == newSize)
			return lhs.clone();

		// Should raise an exception here, this code is nonsensical at present
		if(lhsSize > newSize)
			return lhs.clone();
		
		byte[] newVal = new byte[newSize];
		byte[] lhsVal = lhs.Value();
		int i;
		for (i = 0; i < lhsSize; i++)
			newVal[i] = lhsVal[i];
		for( ; i < newSize; i++)
			newVal[i] = extensionVal;
		return new TVLBitVector(newVal);
	}

	static TVLBitVector ZeroExtend(TVLBitVector lhs, int newSize)
	{
		return Extend(lhs, newSize, TVLBitVector.TVL_0);
	}

	static TVLBitVector ZeroExtend(TVLBitVector lhs, GhidraSizeAdapter gsa)
	{
		return ZeroExtend(lhs, gsa.sz*8);
	}

	static TVLBitVector SignExtend(TVLBitVector lhs, int newSize)
	{
		return Extend(lhs, newSize, lhs.Value()[lhs.Size()-1]);
	}

	static TVLBitVector SignExtend(TVLBitVector lhs, GhidraSizeAdapter gsa)
	{
		return SignExtend(lhs, gsa.sz*8);
	}

	static TVLBitVector CreateSingle(byte what)
	{
		TVLBitVector x = new TVLBitVector(8, 0);
		x.Value()[0] = what;
		return x;
	}
	static TVLBitVector CreateBit(boolean bit)
	{
		return CreateSingle(bit ? TVLBitVector.TVL_1 : TVLBitVector.TVL_0);
	}
	
	static TVLBitVector EqualsInner(TVLBitVector lhs, TVLBitVector rhs, boolean shouldMatch) 
	{
		int s1 = lhs.Size();
		int s2 = rhs.Size();
		if(s1 != s2)
			SizeMismatchException("EqualsInner", s1, s2);
		
		byte[] lhsVal = lhs.Value();
		byte[] rhsVal = rhs.Value();
		boolean bHadHalves = false;
		for (int i = 0; i < s1; i++) {
			byte lhsBit = lhsVal[i];
			byte rhsBit = rhsVal[i];
			if(lhsBit == TVLBitVector.TVL_HALF || rhsBit == TVLBitVector.TVL_HALF)
				bHadHalves = true;
			else if(lhsBit != rhsBit)
				return CreateBit(!shouldMatch);
		}
		if(bHadHalves)
			return CreateSingle(TVLBitVector.TVL_HALF);
		return CreateBit(shouldMatch);
	}
	static TVLBitVector Equals(TVLBitVector lhs, TVLBitVector rhs) 
	{
		return EqualsInner(lhs, rhs, true);
	}
	static TVLBitVector NotEquals(TVLBitVector lhs, TVLBitVector rhs) 
	{
		return EqualsInner(lhs, rhs, false);
	}
	static TVLBitVector ShiftLeftInt(TVLBitVector lhs, int amt)
	{
		int lhsSize = lhs.Size();
	
		if(amt == 0)
			return lhs.clone();
		
		if(amt >= lhsSize)
			return new TVLBitVector(lhsSize, 0);
	
		byte[] newArr = new byte[lhsSize];
		byte[] lhsVal = lhs.Value();
		for (int i = 0; i < amt; i++)
			newArr[i] = TVLBitVector.TVL_0;
	
		for(int j = 0; j < lhsSize-amt; j++)
			newArr[amt+j] = lhsVal[j];
	
		return new TVLBitVector(newArr);
	}
	static TVLBitVector ShiftLeftBv(TVLBitVector lhs, TVLBitVector rhs) 
	{
		int lhsSize = lhs.Size();
		int rhsSize = rhs.Size();
		// Seems like Ghidra guarantees this
		assert(lhsSize != 0 && (lhsSize & (lhsSize-1)) == 0);
		// Okay... so how do I want to handle this
		// Want to extract the log2(lhsSize) lowest bits from rhs
		// If the upper bits are non-zero, return 0
	
		byte[] rhsVal = rhs.Value();
		// I'm sure there's a bit-twiddling hack for log2...
		int log2 = 0;
		for(int i = 1; i < lhsSize; i++) {
			if((lhsSize & (1 << i)) != 0) {
				log2 = i;
				break;
			}
		}
		assert(log2 != 0);
		
		for(int j = log2; j < rhsSize; j++) {
			if(rhsVal[j] != TVLBitVector.TVL_0)
				return new TVLBitVector(lhsSize, 0);
		}
		TVLBitVector shifted = lhs.clone();
		for(int i = 0; i < log2; i++) {
			switch(rhsVal[i])
			{
				case TVLBitVector.TVL_0:
				break;
				case TVLBitVector.TVL_1:
				shifted = ShiftLeftInt(shifted, 1<<i);
				break;
				case TVLBitVector.TVL_HALF:
				TVLBitVector possibleShifted = ShiftLeftInt(shifted, 1<<i);
				shifted = Map2(shifted, possibleShifted, (x,y) -> x == y ? x : TVLBitVector.TVL_HALF);
				break;
			}
		}
		return shifted;
	}
	
	
	static TVLBitVector ShiftRightInt(TVLBitVector lhs, int amt, byte topFill)
	{
		int lhsSize = lhs.Size();
	
		if(amt == 0)
			return lhs.clone();

		if(amt >= lhsSize)
			return Map(lhs, (b) -> topFill);
	
		byte[] newArr = new byte[lhsSize];
		byte[] lhsVal = lhs.Value();
		for (int i = 0; i < amt; i++)
			newArr[(lhsSize-1)-i] = topFill;
	
		for(int j = 0; j < lhsSize-amt; j++)
			newArr[j] = lhsVal[j+amt];
	
		return new TVLBitVector(newArr);
	}
	
	// I don't like the code duplication here from ShiftLeftBv, but it might be
	// more trouble than it's worth to refactor it into a separate class
	static TVLBitVector ShiftRightBvInner(TVLBitVector lhs, TVLBitVector rhs, byte topFill) 
	{
		int lhsSize = lhs.Size();
		int rhsSize = rhs.Size();
		// Seems like Ghidra guarantees this
		assert(lhsSize != 0 && (lhsSize & (lhsSize-1)) == 0);
		// Okay... so how do I want to handle this
		// Want to extract the log2(lhsSize) lowest bits from rhs
		// If the upper bits are non-zero, return 0
	
		byte[] rhsVal = rhs.Value();
		// I'm sure there's a bit-twiddling hack for log2...
		int log2 = 0;
		for(int i = 1; i < lhsSize; i++) {
			if((lhsSize & (1 << i)) != 0) {
				log2 = i;
				break;
			}
		}
		assert(log2 != 0);
		
		for(int j = log2; j < rhsSize; j++) {
			if(rhsVal[j] != TVLBitVector.TVL_0)
				return Map(lhs, (b) -> topFill);
		}
		TVLBitVector shifted = lhs.clone();
		for(int i = 0; i < log2; i++) {
			switch(rhsVal[i])
			{
				case TVLBitVector.TVL_0:
				break;
				case TVLBitVector.TVL_1:
				shifted = ShiftRightInt(shifted, 1<<i, topFill);
				break;
				case TVLBitVector.TVL_HALF:
				TVLBitVector possibleShifted = ShiftRightInt(shifted, 1<<i, topFill);
				shifted = Map2(shifted, possibleShifted, (x,y) -> x == y ? x : TVLBitVector.TVL_HALF);
				break;
			}
		}
		return shifted;
	}

	static TVLBitVector ShiftRightBv(TVLBitVector lhs, TVLBitVector rhs) 
	{
		return ShiftRightBvInner(lhs, rhs, TVLBitVector.TVL_0);
	}

	static TVLBitVector ShiftRightArithmeticBv(TVLBitVector lhs, TVLBitVector rhs) 
	{
		return ShiftRightBvInner(lhs, rhs, lhs.Value()[lhs.Size()-1]);
	}

	// x + y + c ...
	static final byte[][][] AddOutputTable = { 
		// c = 0
		{
			//     y = 0                   y = 1/2                y = 1
			{TVLBitVector.TVL_0,     TVLBitVector.TVL_HALF, TVLBitVector.TVL_1},    // x = 0
			{TVLBitVector.TVL_HALF,  TVLBitVector.TVL_HALF, TVLBitVector.TVL_HALF}, // x = 1/2
			{TVLBitVector.TVL_1,     TVLBitVector.TVL_HALF, TVLBitVector.TVL_0}     // x = 1
		},
		
		// c = 1/2
		{
			//     y = 0                   y = 1/2                y = 1
			{TVLBitVector.TVL_HALF,  TVLBitVector.TVL_HALF, TVLBitVector.TVL_HALF}, // x = 0
			{TVLBitVector.TVL_HALF,  TVLBitVector.TVL_HALF, TVLBitVector.TVL_HALF}, // x = 1/2
			{TVLBitVector.TVL_HALF,  TVLBitVector.TVL_HALF, TVLBitVector.TVL_HALF}  // x = 1
		},

		// c = 1
		{
			//     y = 0                   y = 1/2                y = 1
			{TVLBitVector.TVL_1,     TVLBitVector.TVL_HALF, TVLBitVector.TVL_0},    // x = 0
			{TVLBitVector.TVL_HALF,  TVLBitVector.TVL_HALF, TVLBitVector.TVL_HALF}, // x = 1/2
			{TVLBitVector.TVL_0,     TVLBitVector.TVL_HALF, TVLBitVector.TVL_1}     // x = 1
		},
	};

	// x + y + c (carry part) ...
	static final byte[][][] AddCarryTable = { 
		// c = 0
		{
			//     y = 0                   y = 1/2                y = 1
			{TVLBitVector.TVL_0,     TVLBitVector.TVL_0,    TVLBitVector.TVL_0},    // x = 0
			{TVLBitVector.TVL_0,     TVLBitVector.TVL_HALF, TVLBitVector.TVL_HALF}, // x = 1/2
			{TVLBitVector.TVL_0,     TVLBitVector.TVL_HALF, TVLBitVector.TVL_1}     // x = 1
		},
		
		// c = 1/2
		{
			//     y = 0                   y = 1/2                y = 1
			{TVLBitVector.TVL_0,     TVLBitVector.TVL_HALF, TVLBitVector.TVL_HALF}, // x = 0
			{TVLBitVector.TVL_HALF,  TVLBitVector.TVL_HALF, TVLBitVector.TVL_HALF}, // x = 1/2
			{TVLBitVector.TVL_HALF,  TVLBitVector.TVL_HALF, TVLBitVector.TVL_1}     // x = 1
		},

		// c = 1
		{
			//     y = 0                   y = 1/2                y = 1
			{TVLBitVector.TVL_0,     TVLBitVector.TVL_HALF, TVLBitVector.TVL_1},    // x = 0
			{TVLBitVector.TVL_HALF,  TVLBitVector.TVL_HALF, TVLBitVector.TVL_1},    // x = 1/2
			{TVLBitVector.TVL_1,     TVLBitVector.TVL_1,    TVLBitVector.TVL_1}     // x = 1
		},
	};
	
	static Pair<TVLBitVector,TVLBitVector> AddInternal(TVLBitVector lhs, TVLBitVector rhs, boolean doNot, byte initialCarry) 
	{
		int s1 = lhs.Size();
		int s2 = rhs.Size();
		if(s1 != s2)
			SizeMismatchException("AddInternal", s1, s2);
		TVLBitVector sum      = Map(lhs, (x) -> TVLBitVector.TVL_0);
		TVLBitVector carryVec = Map(lhs, (x) -> TVLBitVector.TVL_0);
		
		if(doNot)
			rhs = Not(rhs);
		
		byte[] lhsArr = lhs.Value();
		byte[] rhsArr = rhs.Value();
		byte[] sumArr = sum.Value();
		byte[] carryArr = carryVec.Value();
		byte lastCarry = initialCarry;
		for(int i = 0; i < s1; i++)
		{
			sumArr[i] = AddOutputTable[lhsArr[i]][rhsArr[i]][lastCarry];
			lastCarry = AddCarryTable [lhsArr[i]][rhsArr[i]][lastCarry];
			carryArr[i] = lastCarry;
		}
		return new Pair<TVLBitVector,TVLBitVector>(sum,carryVec);
	}

	static TVLBitVector Add(TVLBitVector lhs, TVLBitVector rhs) 
	{
		Pair<TVLBitVector,TVLBitVector> p = AddInternal(lhs, rhs, false, TVLBitVector.TVL_0);
		return p.x;
	}

	static TVLBitVector Subtract(TVLBitVector lhs, TVLBitVector rhs) 
	{
		Pair<TVLBitVector,TVLBitVector> p = AddInternal(lhs, rhs, true, TVLBitVector.TVL_1);
		return p.x;
	}
	
	static TVLBitVector Neg(TVLBitVector lhs) 
	{
		TVLBitVector zero = Map(lhs, (x) -> TVLBitVector.TVL_0);
		Pair<TVLBitVector,TVLBitVector> p = AddInternal(zero, lhs, true, TVLBitVector.TVL_1);
		return p.x;
	}

	static TVLBitVector ULT(TVLBitVector lhs, TVLBitVector rhs) 
	{
		Pair<TVLBitVector,TVLBitVector> p = AddInternal(lhs, rhs, true, TVLBitVector.TVL_1);
		TVLBitVector cvec = p.y;
		return CreateSingle(NotTable[cvec.Value()[cvec.Size()-1]]);
	}

	static TVLBitVector ULE(TVLBitVector lhs, TVLBitVector rhs) 
	{
		byte ult = ULT(lhs,rhs).Value()[0];
		byte eq = Equals(lhs,rhs).Value()[0];
		return CreateSingle(OrTable[ult][eq]);
	}

	static TVLBitVector SLT(TVLBitVector lhs, TVLBitVector rhs) 
	{
		byte ult = ULT(lhs,rhs).Value()[0];
		byte lhsSign = lhs.Value()[lhs.Size()-1];
		byte rhsSign = rhs.Value()[rhs.Size()-1];
		return CreateSingle(XorTable[XorTable[lhsSign][rhsSign]][ult]);
	}

	static TVLBitVector SLE(TVLBitVector lhs, TVLBitVector rhs) 
	{
		byte slt = SLT(lhs,rhs).Value()[0];
		byte eq = Equals(lhs,rhs).Value()[0];
		return CreateSingle(OrTable[slt][eq]);
	}
	
	static TVLBitVector WidenDoubleShlInt(TVLBitVector lhs, int amt)
	{
		TVLBitVector widened = ZeroExtend(lhs, lhs.Size()*2);
		return ShiftLeftInt(widened, amt);
	}
	
	static TVLBitVector Multiply(TVLBitVector lhs, TVLBitVector rhs) 
	{
		int s1 = lhs.Size();
		int s2 = rhs.Size();
		if(s1 != s2)
			SizeMismatchException("Multiply", s1, s2);
		
		byte[] rhsArr = rhs.Value();
		TVLBitVector lhsHalves = Map(lhs, (b) -> b == TVLBitVector.TVL_1 ? TVLBitVector.TVL_HALF : b);
		TVLBitVector partialProduct = WidenDoubleShlInt(Map(lhs, (b) -> TVLBitVector.TVL_0), 0);
		for(int i = 0; i < s1; i++) {
			switch(rhsArr[i])
			{
				case TVLBitVector.TVL_0:
				break;
				case TVLBitVector.TVL_1:
				partialProduct = Add(partialProduct, WidenDoubleShlInt(lhs, i));
				break;
				case TVLBitVector.TVL_HALF:
				partialProduct = Add(partialProduct, WidenDoubleShlInt(lhsHalves, i));
				break;
			}
		}
		return new TVLBitVector(Arrays.copyOfRange(partialProduct.Value(), 0, s1));
	}
}

class AbstractMemory {
	HashMap<Long,TVLBitVector> Contents;
	public AbstractMemory() {
		Contents = new HashMap<>();
	}
	
	AbstractMemory Store(long addr, TVLBitVector bv)
	{
		HashMap<Long,TVLBitVector> newContents = (HashMap)Contents.clone();
		newContents.put(addr,bv);
		AbstractMemory newMemory = new AbstractMemory();
		newMemory.Contents = newContents;
		return newMemory;
	}
	void Dump(String str)
	{
		//Printer.println("Dump(): "+str);
		//for (HashMap.Entry<Long,TVLBitVector> entry : Contents.entrySet())  
		//	Printer.println("Key = " + entry.getKey() + ", Value = " + entry.getValue()); 
	}
	AbstractMemory StoreWholeQuantity(long addr, TVLBitVector bv)
	{
		AbstractMemory am = this;
		byte[] bvArr = bv.Value();
		for(int i = 0; i < bv.Size(); i += 8)
		{
			byte[] subArr = Arrays.copyOfRange(bvArr, i, i+8);
			am = am.Store(addr, new TVLBitVector(subArr));
			addr += 1;
		}
		am.Dump("StoreWholeQuantity(): "+addr+" "+bv);
		return am;
	}
	TVLBitVector Lookup(long addr)
	{
		Dump("Lookup(): "+addr);
		if(Contents.containsKey(addr))
			return Contents.get(addr);
		TVLBitVector bv = new TVLBitVector(8);
		return bv;
	}

	// Stolen from StackExchange
	public static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	TVLBitVector LookupWholeQuantity(long addr, int size)
	{
		LinkedList<TVLBitVector> list = new LinkedList<TVLBitVector>(); 
		for(int i = 0; i < size; i += 8)
		{
			list.add(Lookup(addr));
			addr += 1;
		}
		byte[] arr = new byte[size];
		
		int i = 0;
		while(!list.isEmpty())
		{
			
			TVLBitVector current = list.remove();
			System.arraycopy(current.Value(), 0, arr, i*8, 8);
			i++;
		}
		return new TVLBitVector(arr);
	}
	
	TVLBitVector LookupWholeQuantity(long addr, GhidraSizeAdapter gsa)
	{
		return LookupWholeQuantity(addr, gsa.sz*8);
	}
	AbstractMemory Top()
	{
		return new AbstractMemory();
	}
};

class TVLAbstractInterpreter extends PcodeOpVisitor<TVLBitVector> {
	HashMap<Varnode,TVLBitVector> RegisterValueMap;
	HashMap<Varnode,TVLBitVector> UniqueValueMap;
	AbstractMemory Memory;
	
	public TVLAbstractInterpreter()
	{
		RegisterValueMap = new HashMap<>();
		UniqueValueMap   = new HashMap<>();
		Memory           = new AbstractMemory();
	}
	
	TVLBitVector visit_Constant(Instruction instr, PcodeOp pcode, Varnode Constant) 
	{
		return new TVLBitVector(new GhidraSizeAdapter(Constant.getSize()), Constant.getOffset());
	}
	TVLBitVector visit_Register(Instruction instr, PcodeOp pcode, Varnode Register) 
	{
		if(RegisterValueMap.containsKey(Register))
			return RegisterValueMap.get(Register);
		TVLBitVector bv = new TVLBitVector(new GhidraSizeAdapter(Register.getSize()));
		RegisterValueMap.put(Register, bv);
		return bv;
	}
	TVLBitVector visit_Unique(Instruction instr, PcodeOp pcode, Varnode Unique) 
	{
		// This should probably happen 100% of the time...
		if(UniqueValueMap.containsKey(Unique))
			return UniqueValueMap.get(Unique);
		// Should probably throw an exception here...
		Printer.println("visit_Unique(): Unbound unique "+Unique.toString());
		TVLBitVector bv = new TVLBitVector(new GhidraSizeAdapter(Unique.getSize()));
		UniqueValueMap.put(Unique, bv);
		return bv;
	}
	
	void Associate(Varnode dest, TVLBitVector bv)
	{
		//Printer.println("Associate(): "+dest.toString()+" -> "+bv.toString());
		if(dest.isRegister())
			RegisterValueMap.put(dest, bv);
		else if(dest.isUnique())
			UniqueValueMap.put(dest,bv);
		else
		{
			Printer.println("Associate(): Unknown destination "+dest.toString());
			// Should throw an exception here...
		}
	}

	void visit_BOOL_AND(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException
	{
		TVLBitVector lhs = visit_Varnode(instr,pcode,pcode.getInput(0));
		TVLBitVector rhs = visit_Varnode(instr,pcode,pcode.getInput(1));
		TVLBitVector result = TVLBitVectorUtil.CreateSingle(TVLBitVectorUtil.AndTable[lhs.Value()[0]][rhs.Value()[0]]);
		Associate(pcode.getOutput(), result);
	}
	void visit_BOOL_NEGATE(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException 
	{
		TVLBitVector lhs = visit_Varnode(instr,pcode,pcode.getInput(0));
		TVLBitVector result = TVLBitVectorUtil.CreateSingle(TVLBitVectorUtil.NotTable[lhs.Value()[0]]);
		Associate(pcode.getOutput(), result);
	}; 
	void visit_BOOL_OR(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException 
	{
		TVLBitVector lhs = visit_Varnode(instr,pcode,pcode.getInput(0));
		TVLBitVector rhs = visit_Varnode(instr,pcode,pcode.getInput(1));
		TVLBitVector result = TVLBitVectorUtil.CreateSingle(TVLBitVectorUtil.OrTable[lhs.Value()[0]][rhs.Value()[0]]);
		Associate(pcode.getOutput(), result);
	}; 
	void visit_BOOL_XOR(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException 
	{
		TVLBitVector lhs = visit_Varnode(instr,pcode,pcode.getInput(0));
		TVLBitVector rhs = visit_Varnode(instr,pcode,pcode.getInput(1));
		TVLBitVector result = TVLBitVectorUtil.CreateSingle(TVLBitVectorUtil.XorTable[lhs.Value()[0]][rhs.Value()[0]]);
		Associate(pcode.getOutput(), result);
	}; 
	void visit_COPY(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException 
	{
		TVLBitVector lhs = visit_Varnode(instr,pcode,pcode.getInput(0));
		Associate(pcode.getOutput(), lhs);		
	}; 

	void visit_INT_2COMP(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException 
	{ 
		TVLBitVector lhs = visit_Varnode(instr,pcode,pcode.getInput(0));
		TVLBitVector result = TVLBitVectorUtil.Neg(lhs);
		Associate(pcode.getOutput(), result);		
	}; 
	void visit_INT_ADD(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException
	{
		TVLBitVector lhs = visit_Varnode(instr,pcode,pcode.getInput(0));
		TVLBitVector rhs = visit_Varnode(instr,pcode,pcode.getInput(1));
		TVLBitVector result = TVLBitVectorUtil.Add(lhs,rhs);
		Associate(pcode.getOutput(), result);
	}; 
	void visit_INT_AND(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException
	{
		TVLBitVector lhs = visit_Varnode(instr,pcode,pcode.getInput(0));
		TVLBitVector rhs = visit_Varnode(instr,pcode,pcode.getInput(1));
		TVLBitVector result = TVLBitVectorUtil.And(lhs,rhs);
		Associate(pcode.getOutput(), result);
	}; 
	void visit_INT_CARRY(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_CARRY"); }; 
	void visit_INT_DIV(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_DIV"); }; 
	void visit_INT_EQUAL(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException
	{
		TVLBitVector lhs = visit_Varnode(instr,pcode,pcode.getInput(0));
		TVLBitVector rhs = visit_Varnode(instr,pcode,pcode.getInput(1));
		TVLBitVector result = TVLBitVectorUtil.Equals(lhs,rhs);
		Associate(pcode.getOutput(), result);
	}; 
	void visit_INT_LEFT(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException 
	{
		TVLBitVector lhs = visit_Varnode(instr,pcode,pcode.getInput(0));
		TVLBitVector rhs = visit_Varnode(instr,pcode,pcode.getInput(1));
		TVLBitVector result = TVLBitVectorUtil.ShiftLeftBv(lhs,rhs);
		Associate(pcode.getOutput(), result);
	}; 
	void visit_INT_LESS(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException
	{
		TVLBitVector lhs = visit_Varnode(instr,pcode,pcode.getInput(0));
		TVLBitVector rhs = visit_Varnode(instr,pcode,pcode.getInput(1));
		TVLBitVector result = TVLBitVectorUtil.ULT(lhs,rhs);
		Associate(pcode.getOutput(), result);
	}; 
	void visit_INT_LESSEQUAL(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException
	{
		TVLBitVector lhs = visit_Varnode(instr,pcode,pcode.getInput(0));
		TVLBitVector rhs = visit_Varnode(instr,pcode,pcode.getInput(1));
		TVLBitVector result = TVLBitVectorUtil.ULE(lhs,rhs);
		Associate(pcode.getOutput(), result);
	}; 
	void visit_INT_MULT(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException
	{
		TVLBitVector lhs = visit_Varnode(instr,pcode,pcode.getInput(0));
		TVLBitVector rhs = visit_Varnode(instr,pcode,pcode.getInput(1));
		TVLBitVector result = TVLBitVectorUtil.Multiply(lhs,rhs);
		Associate(pcode.getOutput(), result);
	}; 
	void visit_INT_NEGATE(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException 
	{
		TVLBitVector lhs = visit_Varnode(instr,pcode,pcode.getInput(0));
		TVLBitVector result = TVLBitVectorUtil.Not(lhs);
		Associate(pcode.getOutput(), result);
	}; 
	void visit_INT_NOTEQUAL(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException
	{
		TVLBitVector lhs = visit_Varnode(instr,pcode,pcode.getInput(0));
		TVLBitVector rhs = visit_Varnode(instr,pcode,pcode.getInput(1));
		TVLBitVector result = TVLBitVectorUtil.NotEquals(lhs,rhs);
		Associate(pcode.getOutput(), result);
	}; 
	void visit_INT_OR(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException
	{
		TVLBitVector lhs = visit_Varnode(instr,pcode,pcode.getInput(0));
		TVLBitVector rhs = visit_Varnode(instr,pcode,pcode.getInput(1));
		TVLBitVector result = TVLBitVectorUtil.Or(lhs,rhs);
		Associate(pcode.getOutput(), result);
	}; 
	void visit_INT_REM(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_REM"); }; 
	void visit_INT_RIGHT(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException
	{
		TVLBitVector lhs = visit_Varnode(instr,pcode,pcode.getInput(0));
		TVLBitVector rhs = visit_Varnode(instr,pcode,pcode.getInput(1));
		TVLBitVector result = TVLBitVectorUtil.ShiftRightBv(lhs,rhs);
		Associate(pcode.getOutput(), result);
	}; 
	void visit_INT_SBORROW(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_SBORROW"); }; 
	void visit_INT_SCARRY(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_SCARRY"); }; 
	void visit_INT_SDIV(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_SDIV"); }; 
	void visit_INT_SEXT(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException 
	{
		TVLBitVector lhs = visit_Varnode(instr,pcode,pcode.getInput(0));
		Varnode output = pcode.getOutput();
		TVLBitVector result = TVLBitVectorUtil.SignExtend(lhs, new GhidraSizeAdapter(output.getSize()));
		Associate(output, result);
	}; 
	void visit_INT_SLESS(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException
	{
		TVLBitVector lhs = visit_Varnode(instr,pcode,pcode.getInput(0));
		TVLBitVector rhs = visit_Varnode(instr,pcode,pcode.getInput(1));
		TVLBitVector result = TVLBitVectorUtil.SLT(lhs,rhs);
		Associate(pcode.getOutput(), result);
	}; 
	void visit_INT_SLESSEQUAL(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException
	{
		TVLBitVector lhs = visit_Varnode(instr,pcode,pcode.getInput(0));
		TVLBitVector rhs = visit_Varnode(instr,pcode,pcode.getInput(1));
		TVLBitVector result = TVLBitVectorUtil.SLE(lhs,rhs);
		Associate(pcode.getOutput(), result);
	}; 
	void visit_INT_SREM(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException { VisitorUnimplemented("INT_SREM"); }; 
	void visit_INT_SRIGHT(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException 
	{ 
		TVLBitVector lhs = visit_Varnode(instr,pcode,pcode.getInput(0));
		TVLBitVector rhs = visit_Varnode(instr,pcode,pcode.getInput(1));
		TVLBitVector result = TVLBitVectorUtil.ShiftRightArithmeticBv(lhs,rhs);
		Associate(pcode.getOutput(), result);
	}; 
	void visit_INT_SUB(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException
	{
		TVLBitVector lhs = visit_Varnode(instr,pcode,pcode.getInput(0));
		TVLBitVector rhs = visit_Varnode(instr,pcode,pcode.getInput(1));
		TVLBitVector result = TVLBitVectorUtil.Subtract(lhs,rhs);
		Associate(pcode.getOutput(), result);
	}; 
	void visit_INT_XOR(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException 
	{
		TVLBitVector lhs = visit_Varnode(instr,pcode,pcode.getInput(0));
		TVLBitVector rhs = visit_Varnode(instr,pcode,pcode.getInput(1));
		TVLBitVector result = TVLBitVectorUtil.Xor(lhs,rhs);
		Associate(pcode.getOutput(), result);
	}; 
	void visit_INT_ZEXT(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException 
	{
		TVLBitVector lhs = visit_Varnode(instr,pcode,pcode.getInput(0));
		Varnode output = pcode.getOutput();
		TVLBitVector result = TVLBitVectorUtil.ZeroExtend(lhs, new GhidraSizeAdapter(output.getSize()));
		Associate(output, result);
	}; 
	void visit_LOAD(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException 
	{ 
		TVLBitVector addr = visit_Varnode(instr,pcode,pcode.getInput(1));
		Varnode output = pcode.getOutput();
		Pair<Integer,Long> p = addr.GetConstantValue();
		TVLBitVector result;
		if(p == null)
			result = new TVLBitVector(new GhidraSizeAdapter(output.getSize()));
		else
			result = Memory.LookupWholeQuantity(p.y, new GhidraSizeAdapter(output.getSize()));
		Associate(output, result);
	}; 
	void visit_STORE(Instruction instr, PcodeOp pcode) throws VisitorUnimplementedException 
	{
		TVLBitVector addr = visit_Varnode(instr,pcode,pcode.getInput(1));
		TVLBitVector what = visit_Varnode(instr,pcode,pcode.getInput(2));
		Pair<Integer,Long> p = addr.GetConstantValue();
		if(p != null)
			Memory = Memory.StoreWholeQuantity(p.y, what);
		else
			Memory = Memory.Top();
	}; 
	
}

public class ThreeValuedAbstractInterpreter extends GhidraScript {

	void AbstractInterpret(InstructionIterator instructions, boolean TF) throws Exception
	{
		TVLAbstractInterpreter visitor = new TVLAbstractInterpreter();
		Language l = currentProgram.getLanguage();
		Register rESP = l.getRegister("ESP");
		Register rTF  = l.getRegister("TF");
		Register rAL  = l.getRegister("AL");
		//Register rMEM = l.getRegister("MEM");
		VarnodeTranslator vt = new VarnodeTranslator​(currentProgram);
		Varnode vESP = vt.getVarnode(rESP);
		Varnode vTF  = vt.getVarnode(rTF);
		Varnode vAL  = vt.getVarnode(rAL);
		//Varnode vMEM = vt.getVarnode(rMEM);
		int tfValue = TF ? 1 : 0;
		println("Analyzing under the assumption that TF ="+TF);
		visitor.RegisterValueMap.put(vESP, new TVLBitVector(32, 0x1000));
		visitor.RegisterValueMap.put(vTF,  new TVLBitVector(8, tfValue));
		try {
			while (instructions.hasNext()) {
				monitor.checkCanceled();
				Instruction instr = instructions.next();

				PcodeOp[] pcode = instr.getPcode();

				for (int i = 0; i < pcode.length; i++) {
					//println(pcode[i].toString());
					Varnode	output = pcode[i].getOutput();
					//if(output != null)
					//	println("\t" + output.toString());
					Varnode[]	inputs = pcode[i].getInputs();
					visitor.visit(instr,pcode[i]);
					for (int j = 0; j < inputs.length; ++j)
					{
						//println("\t"+j+" "+inputs[j].toString());
						//if(inputs[j].isConstant())
						//	println("\t\tConstant value?"+inputs[j].getOffset());
					}
				
				}
				visitor.UniqueValueMap.clear();
			}
			println("Final value of AL: "+visitor.RegisterValueMap.get(vAL));
		}
		catch(VisitorUnimplementedException e)
		{
			println("Caught visitor unimplemented exception: "+e);
		}
		
	}
	
	@Override
	public void run() throws Exception {
		if (currentProgram == null) {
			return;
		}
		PluginTool tool = state.getTool();
		Printer.Set(tool.getService(ConsoleService.class));
		AddressSetView set = currentSelection;
		if (set == null || set.isEmpty()) {
			set = currentProgram.getMemory().getExecuteSet();
		}
		AbstractInterpret(currentProgram.getListing().getInstructions(set, true), false);
		AbstractInterpret(currentProgram.getListing().getInstructions(set, true), true);
	}
}
