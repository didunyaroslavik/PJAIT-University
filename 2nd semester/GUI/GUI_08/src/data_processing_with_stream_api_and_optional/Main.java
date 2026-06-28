package data_processing_with_stream_api_and_optional;

import data_processing_with_stream_api_and_optional.tasks.P1_CreatingStreams;
import data_processing_with_stream_api_and_optional.tasks.P2_FilterAndMap;
import data_processing_with_stream_api_and_optional.tasks.P3_SortLimitSkip;
import data_processing_with_stream_api_and_optional.tasks.P4_TerminalOperations;
import data_processing_with_stream_api_and_optional.tasks.P5_CollectBasic;

public class Main {

    public static void main(String[] args) {
        P1_CreatingStreams.go();
        P2_FilterAndMap.go();
        P3_SortLimitSkip.go();
        P4_TerminalOperations.go();
        P5_CollectBasic.go();
    }
}
