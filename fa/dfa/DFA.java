package fa.dfa;

import fa.State;
import java.util.*;

/**
 * DFA class
 * Deterministic verison of FA
 * @author Daniel Heck and Brooklyn Grant
 */
public class DFA implements DFAInterface {

    private Set<DFAState> states = new LinkedHashSet<>(); // state list
    private Set<Character> sigma = new LinkedHashSet<>(); // alphabet
    private DFAState startState;

    /**
     * Constructor
     */
    public DFA() {

    }

    @Override
    public boolean addState(String name) {
        if (getState(name) == null) { // make sure there is no other state with the name
            states.add(new DFAState(name));
            return true; // pass
        }
        return false; // fail
    }

    @Override
    public boolean setFinal(String name) {
        DFAState state = getState(name);
        if (state != null) {
            state.setFinal(true);
            return true; // pass
        }
        return false; // fail
    }

    @Override
    public boolean setStart(String name) {
        if (getState(name) != null) { // make sure it is in states before looping
            for (DFAState s : states) {
                if (s.getName().equals(name)) {
                    s.setStarting(true);
                    startState = s;
                } else {
                    s.setStarting(false); // make sure only one is starting, set others to false
                }
            }
            return true; // pass
        }
        return false; // fail
    }

    @Override
    public void addSigma(char symbol) {
        sigma.add(symbol);
    }

    @Override
    public boolean accepts(String s) {
    	DFAState currentState = startState;
        for(int i = 0; i < s.length(); i++) {
        	currentState = getState(currentState.getTransition(s.charAt(i)));
        }
        if(currentState != null && currentState.isFinal()) {
        	return true;
        }
    	return false;
    }

    @Override
    public Set<Character> getSigma() {
        return sigma;
    }

    @Override
    public DFAState getState(String name) {
        for (DFAState s : states) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        return null; // could not find
    }

    @Override
    public boolean isFinal(String name) {
        DFAState state = getState(name);
        if (state != null) {
            return state.isFinal();
        }
        return false; // doesn't exist
    }

    @Override
    public boolean isStart(String name) {
        DFAState state = getState(name);
        if (state != null) {
            return state.isStarting();
        }
        return false; // doesn't exist
    }

    @Override
    public boolean addTransition(String fromState, String toState, char onSymb) {
        DFAState fState = getState(fromState);
        DFAState tState = getState(toState);
        if (fState != null && tState != null && isValidSigma(onSymb)) {
            fState.setTransition(onSymb, toState);
            return true; // pass
        }
        return false; // fail
    }

    @Override
    public DFA swap(char symb1, char symb2) {
        if (!isValidSigma(symb1) || !isValidSigma(symb2)) {
            return null; // symbols are not in alphabet so cannot swap
        }

        DFA newDfa = new DFA();
        // Add sigma
        for (char c : sigma) {
            newDfa.addSigma(c);
        }

        // Add new states and do swap on transitions
        for (DFAState oldState : states) {
            // Add State
            newDfa.addState(oldState.getName());

            // Get newly added state and swap transitions
            DFAState state = newDfa.getState(oldState.getName());

            oldState.cloneTransitions(state); // clone transitions

            String temp = oldState.getTransition(symb2);
            state.setTransition(symb2, oldState.getTransition(symb1));
            state.setTransition(symb1, temp);
        }

        // Start state
        newDfa.setStart(startState.getName());

        // Final states
        for (DFAState s : states) {
            if (s.isFinal()) {
                newDfa.setFinal(s.getName());
            }
        }

        return newDfa;
    }

    /**
     * Tests if a symbol is a valid member of the alphabet
     * @param symb
     * @return true if it is in the alphabet
     */
    private boolean isValidSigma(char symb) {
        return sigma.contains(symb);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();

        // Get 5-tuple as string

        // States (Q)
        ret.append("Q = { ");
        for (DFAState s : states) {
            ret.append(s.getName() + " ");
        }
        ret.append("}\n");

        // Sigma
        ret.append("Sigma = { ");
        for (Character s : sigma) {
            ret.append(s + " ");
        }
        ret.append("}\n");

        // Transition table (delta)
        ret.append("delta = \n");
        for(Character s: sigma) {
        	ret.append("\t" + s);
        }
        ret.append("\n");
        for(DFAState s: states) {
        	ret.append(s + "\t");
        	for(Character t: sigma) {
        		ret.append(s.getTransition(t) + "\t");
        	}
        	ret.append("\n");
        }

        // Start State (q0)
        ret.append("q0 = ");
        for (DFAState s : states) {
            if (s.isStarting()) {
                ret.append(s.getName());
                break; // found start, don't need to continue
            }
        }
        ret.append("\n");

        // Final States (F)
        ret.append("F = { ");
        for (DFAState s : states) {
            if (s.isFinal()) {
                ret.append(s.getName() + " ");
            }
        }
        ret.append("}\n");

        return ret.toString();
    }
}