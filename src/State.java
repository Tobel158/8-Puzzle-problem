//package main;


public class State implements Cloneable{

    private int[][] tile_seq;
    private int depth = 0;
    private int weight = 0;


    public int getWeight() {
        return weight;
    }


    public void setWeight(int weight) {
        this.weight = weight;
    }


    public int[][] getTile_seq() {
        return tile_seq;
    }


    public void setTile_seq(int[][] tile_seq) {
        this.tile_seq = tile_seq;
    }


    public int getDepth() {
        return depth;
    }


    public void setDepth(int depth) {
        this.depth = depth;
    }

    public State() {
        super();
    }


    public State(int[][] tile_seq, int depth) {
        super();
        this.tile_seq = tile_seq;
        this.depth = depth;
    }


    public State(int[][] tile_seq, int depth, int weight) {
        super();
        this.tile_seq = tile_seq;
        this.depth = depth;
        this.weight = weight;
    }

    public int[] getTile_1d(){
        int [] tiles = new int[tile_seq.length*tile_seq[0].length];
        int index = 0;
        for(int i = 0 ; i<tile_seq.length ; i++){
            for(int j = 0 ; j<tile_seq[i].length ; j++){
                tiles[index] = tile_seq[i][j];
                index ++;
            }
        }
        return tiles;
    }


    @Override
    public boolean equals(Object obj) {
        int [][] op= ((State)obj).getTile_seq();

        for(int i = 0 ; i<tile_seq.length ; i++){
            for(int j = 0 ; j<tile_seq[i].length ; j++){
                if(tile_seq[i][j] != op[i][j]){
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }


}
