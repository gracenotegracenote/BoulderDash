package parser;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.Direction;
import models.OriginalBaustein;
import models.Regel;
import models.ResultBaustein;
import models.Situation;

/**
 * Created by gracenote on 13-Dec-16.
 */
public class RuleJson {
	@SerializedName("probability")
	private double probability;
	@SerializedName("situation")
    private String situation;
	@SerializedName("direction")
    private String direction;
	@SerializedName("original")
    private Token[] original;
	@SerializedName("result")
    private Token[] result;


    public Regel toRegel() {
        Situation sit = Situation.fromString(situation);
        Direction dir = Direction.fromString(direction);
        List<OriginalBaustein> orig = convertOriginal(original);
        List<ResultBaustein> res = convertResult(result);

        return new Regel(probability, sit, dir, orig, res);
    }


    private List<OriginalBaustein> convertOriginal(Token[] tokens) {
        List<OriginalBaustein> list = new ArrayList<>();

        for (Token t : tokens) {
            list.add((OriginalBaustein)t.toOriginal());
        }

        return list;
    }


    private List<ResultBaustein> convertResult(Token[] tokens) {
        List<ResultBaustein> list = new ArrayList<>();

        for (Token t : tokens) {
            list.add((ResultBaustein)t.toResult());
        }

        return list;
    }


	public double getProbability() {
		return probability;
	}


	public void setProbability(double probability) {
		this.probability = probability;
	}


	public String getSituation() {
        return situation;
    }


    public void setSituation(String situation) {
        this.situation = situation;
    }


    public String getDirection() {
        return direction;
    }


    public void setDirection(String direction) {
        this.direction = direction;
    }


    public Token[] getOriginal() {
        return original;
    }


    public void setOriginal(Token[] original) {
        this.original = original;
    }


    public Token[] getResult() {
        return result;
    }


    public void setResult(Token[] result) {
        this.result = result;
    }


    @Override
    public String toString() {
        return "RuleJson{" +
				"probability=" + probability + '\'' +
                "situation='" + situation + '\'' +
                ", direction='" + direction + '\'' +
                ", \noriginal=" + Arrays.toString(original) +
                ", \nresult=" + Arrays.toString(result) +
                '}';
    }
}
