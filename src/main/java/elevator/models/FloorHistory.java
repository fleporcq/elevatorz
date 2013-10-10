package elevator.models;

import java.util.LinkedList;

public class FloorHistory extends LinkedList<Integer> {

    private static final int LIMIT = 3;

    public boolean hasCycle(){
        boolean cycle = false;
        if(this.size() == LIMIT){
            Integer first = this.get(0);
            Integer second = this.get(1);
            Integer third = this.get(2);
            if(first != null && first.equals(third) && !first.equals(second)){
                cycle = true;
            }
        }
        return cycle;
    }

    @Override
    public boolean add(Integer floor) {
        super.add(floor);
        while (size() > LIMIT) { super.remove(); }
        return true;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder("[");
        int count = 0;
        for (Integer floor:this){
            sb.append(floor);
            count++;
            if(count < this.size()){
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}