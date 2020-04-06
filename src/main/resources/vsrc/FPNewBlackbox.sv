module FPNewBlackbox #(
    // fpu features
    parameter FLEN = 64,
    parameter ENABLE_VECTORS = 0,
    parameter ENABLE_NAN_BOX = 0,
    parameter ENABLE_FP32 = 0,
    parameter ENABLE_FP64 = 0,
    parameter ENABLE_FP16 = 0,
    parameter ENABLE_FP8 = 0,
    parameter ENABLE_FP16ALT = 0,
    parameter ENABLE_INT8 = 0,
    parameter ENABLE_INT16 = 0,
    parameter ENABLE_INT32 = 0,
    parameter ENABLE_INT64 = 0,
    // fpu implementation: default
    // tag type: logic array
    parameter TAG_WIDTH = 0,
    // Do not change, follow fp-new definition
    localparam int unsigned WIDTH        = FLEN,
    localparam int unsigned NUM_OPERANDS = 3,
    localparam type TagType = logic [TAG_WIDTH-1:0]

) (
    // Copied from fpnew_top
    input logic                               clk_i,
    input logic                               rst_ni,
    // Input signals
    input logic [NUM_OPERANDS-1:0][WIDTH-1:0] operands_i,
    input fpnew_pkg::roundmode_e              rnd_mode_i,
    input fpnew_pkg::operation_e              op_i,
    input logic                               op_mod_i,
    input fpnew_pkg::fp_format_e              src_fmt_i,
    input fpnew_pkg::fp_format_e              dst_fmt_i,
    input fpnew_pkg::int_format_e             int_fmt_i,
    input logic                               vectorial_op_i,
    input TagType                             tag_i,
    // Input Handshake
    input  logic                              in_valid_i,
    output logic                              in_ready_o,
    input  logic                              flush_i,
    // Output signals
    output logic [WIDTH-1:0]                  result_o,
    output fpnew_pkg::status_t                status_o,
    output TagType                            tag_o,
    // Output handshake
    output logic                              out_valid_o,
    input  logic                              out_ready_i,
    // Indication of valid data in flight
    output logic                              busy_o
);

    localparam fpnew_pkg::fpu_features_t Features = '{
        Width: FLEN,
        EnableVectors: ENABLE_VECTORS,
        EnableNanBox: ENABLE_NAN_BOX,
        FpFmtMask: (ENABLE_FP32 << 4) | (ENABLE_FP64 << 3) | (ENABLE_FP16 << 2) | (ENABLE_FP8 << 1) | (ENABLE_FP16ALT << 0),
        IntFmtMask: (ENABLE_INT8 << 3) | (ENABLE_INT16 << 2) | (ENABLE_INT32 << 1) | (ENABLE_INT64 << 0)
    };

    fpnew_top #(
        .Features(Features),
        .TagType(TagType)
    ) inst (
        .clk_i(clk_i),
        .rst_ni(rst_ni),

        .operands_i(operands_i),
        .rnd_mode_i(rnd_mode_i),
        .op_i(op_i),
        .op_mod_i(op_mod_i),
        .src_fmt_i(src_fmt_i),
        .dst_fmt_i(dst_fmt_i),
        .int_fmt_i(int_fmt_i),
        .vectorial_op_i(vectorial_op_i),
        .tag_i(tag_i),

        .in_valid_i(in_valid_i),
        .in_ready_o(in_ready_o),
        .flush_i(flush_i),

        .result_o(result_o),
        .status_o(status_o),
        .tag_o(tag_o),

        .out_valid_o(out_valid_o),
        .out_ready_i(out_ready_i),
        
        .busy_o(busy_o)
    );


endmodule
